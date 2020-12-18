package top.chris.shop.web.controller;

import lombok.extern.java.Log;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.chris.shop.common.ShopProperties;
import top.chris.shop.pojo.BuyCart;
import top.chris.shop.pojo.CartItem;
import top.chris.shop.pojo.Users;
import top.chris.shop.pojo.vo.CartItemVo;
import top.chris.shop.pojo.vo.RenderItemInfoVo;
import top.chris.shop.pojo.vo.ShopCartVo;
import top.chris.shop.pojo.vo.UsersVo;
import top.chris.shop.service.CartService;
import top.chris.shop.service.ItemsService;
import top.chris.shop.util.CookieUtils;
import top.chris.shop.util.JsonResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@Log
@RequestMapping("/shopcart")
public class BuyCartController {
    @Autowired
    private ShopProperties shopProperties;

    @Autowired
    private ItemsService itemsService;

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public JsonResult addCart(String itemId, Integer amount,String specId, HttpServletRequest request, HttpServletResponse response) {
        String result = "";
        System.out.println("---接受的商品ID" + itemId + "---购买的数量" + amount);
        //根据商品Id获得商品数据信息
        RenderItemInfoVo vo = itemsService.queryCartInfoByitemIdAndSpecId(itemId,specId);
        Integer stock = itemsService.queryItemStockByItemId(specId);
        if (stock < amount) {
            result = "没有库存了";
        }
        //创建一个新的购物项对象
        CartItemVo cartItemVo = new CartItemVo();
        cartItemVo.setItemId(itemId);
        cartItemVo.setItemName(vo.getItem().getItemName()); //大类名称
        for (RenderItemInfoVo.SimpleItemsImg itemsImg : vo.getItemImgList()) {
            if (itemsImg.getIsMain() == 1){
                cartItemVo.setItemImgUrl(itemsImg.getUrl());
            }
        }
        for (RenderItemInfoVo.SimpleItemsSpec itemsSpec : vo.getItemSpecList()) { //这里要非常注意：要根据传入的specId查询！因为同一个ItemID下（大类），还区不同口味的小类（这些小类也是一个独立的购物项）
            cartItemVo.setSpecId(itemsSpec.getId());
            cartItemVo.setSpecName(itemsSpec.getName());
            cartItemVo.setPriceDiscount(itemsSpec.getPriceDiscount());
            cartItemVo.setPriceNormal(itemsSpec.getPriceNormal());
        }
        cartItemVo.setBuyCounts(amount);
        //将购物项添加到用户存储在Session中的购物车内
        BuyCart buyCart = getBuyCart(request,response);
        buyCart.addCartItem(cartItemVo);
        log.info("--------------打印Session购物车的数据"+ ReflectionToStringBuilder.toString(buyCart));
        //判断用户是否登录
        HttpSession session = request.getSession(false);
        UsersVo user = (UsersVo) session.getAttribute("user");
        log.info("用登录的用户信息:"+ReflectionToStringBuilder.toString(user));
        if (user.getUsername() != null){
            //用户已经登录
            //将该用户存储在Session中的购物车的数据添加到数据库中
            cartService.addCartInDB(buyCart,user.getId());
            log.info("用登录的用户成功把购物车数据插入的cart表中");

        }
        log.info("--------------打印Session购物车的数据"+ ReflectionToStringBuilder.toString(buyCart));
        return JsonResult.isOk();
    }

    @GetMapping("/refresh")
    public JsonResult renderShopCart(HttpServletRequest request, HttpServletResponse response){
        //获取该用户Session中的状态
        HttpSession session = request.getSession(false);
        UsersVo user = (UsersVo) session.getAttribute("user");
        return JsonResult.isOk(cartService.queryCartByUserId(user.getId()));
    }

    @PostMapping("/del")
    public JsonResult delCartItemBySpecId(String itemSpecId,HttpServletRequest request, HttpServletResponse response){
        //根据商品属性id获取ItemSpec对象中商品的id
        String itemId = itemsService.queryItemIdByItemSpecId(itemSpecId);
        //获取Session,false代表：不创建session对象，只是从request中获取。
        HttpSession session = request.getSession(false);
        BuyCart buyCart = (BuyCart) session.getAttribute(shopProperties.getShopCarCookieName());
        //Session中的购物车为空,因为服务器重启后Session的内容自动清空，由于购物车已经写在了数据库中，因此当服务器重新启动后，直接操作数据库删除，不用删除Session中的购物车，因为都没有
        if (buyCart == null){
            log.info("Session中的购物车为空,因为服务器重启后Session的内容自动清空，由于购物车已经写在了数据库中，因此当服务器重新启动后，直接操作数据库删除，不用删除Session中的购物车，因为都没有");
            //获取登录用户的信息
            UsersVo user = (UsersVo) session.getAttribute("user");
            //构建购物车的id
            String id = itemId+":"+itemSpecId;
            //根据购物车id和userId从数据库中删除用户的指定的购物车商品
            cartService.delCartItemById(user.getId(),id);
        }else { //删除Session中某个购物项，同时删除数据库中的对应数据
            buyCart.removeCartItem(itemId, itemSpecId);
            //获取登录用户的信息
            UsersVo user = (UsersVo) session.getAttribute("user");
            //构建购物车的id
            String id = itemId+":"+itemSpecId;
            //根据购物车id和userId从数据库中删除用户的指定的购物车商品
            cartService.delCartItemById(user.getId(),id);
        }
        return JsonResult.isOk();
    }
    @GetMapping("/delAll")
    public JsonResult  delBuyCart(HttpServletRequest request, HttpServletResponse response){
        //false代表：不创建session对象，只是从request中获取。
        HttpSession session = request.getSession(false);
        //清空购物车，清除服务器中的Session
        BuyCart buyCart = (BuyCart)session.getAttribute(shopProperties.getShopCarCookieName());
        if (buyCart == null){
            //获取登录用户的信息
            UsersVo user = (UsersVo) session.getAttribute("user");
            //根据用户userId从数据库中删除用户的指定的购物车商品
            cartService.delAllCartItems(user.getId());
        }else {
            buyCart.clearBuyCart();
            //去除Session中存储的购物车
            session.removeAttribute(shopProperties.getShopCarCookieName());
            //获取登录用户的信息
            UsersVo user = (UsersVo) session.getAttribute("user");
            //根据用户userId从数据库中删除用户的指定的购物车商品
            cartService.delAllCartItems(user.getId());
        }
        //清空购物车，清除浏览器中的cookie
        CookieUtils.setCookie(request,response,shopProperties.getShopCarCookieName(),"");
        return JsonResult.isOk("已清空购物车");
    }


    /**
     * 从Session中获取用户的购物车
     * @param request
     * @param response
     * @return
     */
    public BuyCart getBuyCart(HttpServletRequest request, HttpServletResponse response){
        //创建一个用户独立的Session，用Session保存每一位用户独自的购物车，该购物车不会随着商品的添加而再次创建出一个新的购物车对象，而是使用存储再Session中的用户独立购物车
        HttpSession session = request.getSession(); //每一个用户都有自己的Session，不同用户进来Session取的都是不同Session
        //获取用户的存储在Session中的购物车(新用户：肯定没有购物车需要创建新的；老用户：可以直接获得之前存储在Session中的购物车)
        BuyCart buyCart = (BuyCart)session.getAttribute(shopProperties.getShopCarCookieName());
        log.info("查看每一位用户进来后台是创建的Session ID："+session.getId());
        //如果用户第一使用购物车，那么Session中一开始没有购物车，那就为该用户new一个独立的购物车。
        if (buyCart == null){
            //为用户添加一辆独立的购物车
            buyCart = new BuyCart();
            //将用户创建的购物车放入Session中
            session.setAttribute(shopProperties.getShopCarCookieName(),buyCart);
        }
        return buyCart;
    }

}