package top.chris.shop.web.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.chris.shop.common.ShopProperties;
import top.chris.shop.pojo.BuyCart;
import top.chris.shop.pojo.vo.CartItem;
import top.chris.shop.pojo.vo.RenderItemInfoVo;
import top.chris.shop.pojo.vo.ShopCartVo;
import top.chris.shop.service.ItemsService;
import top.chris.shop.util.CookieUtils;
import top.chris.shop.util.JsonResult;

import javax.servlet.http.Cookie;
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
        CartItem cartItem = new CartItem();
        cartItem.setItemId(itemId);
        cartItem.setItemName(vo.getItem().getItemName()); //大类名称
        for (RenderItemInfoVo.SimpleItemsImg itemsImg : vo.getItemImgList()) {
            if (itemsImg.getIsMain() == 1){
                cartItem.setItemImgUrl(itemsImg.getUrl());
            }
        }
        for (RenderItemInfoVo.SimpleItemsSpec itemsSpec : vo.getItemSpecList()) { //这里要非常注意：要根据传入的specId查询！因为同一个ItemID下（大类），还区不同口味的小类（这些小类也是一个独立的购物项）
            cartItem.setSpecId(itemsSpec.getId());
            cartItem.setSpecName(itemsSpec.getName());
            cartItem.setPriceDiscount(itemsSpec.getPriceDiscount());
            cartItem.setPriceNormal(itemsSpec.getPriceNormal());
        }
        cartItem.setBuyCounts(amount);
        //将购物项添加到用户存储在Session中的购物车内
        BuyCart buyCart = getBuyCart(request,response);
        buyCart.addCartItem(cartItem);
        log.info("--------------打印Session购物车的数据"+ ReflectionToStringBuilder.toString(buyCart));
        return JsonResult.isOk();
    }

    @GetMapping("/refresh")
    public JsonResult renderShopCart(HttpServletRequest request, HttpServletResponse response){
        //获取该用户Session中存储的购物车
        BuyCart buyCart = getBuyCart(request, response);
        //构造前端输出的ShopCartVo集合对象
        Map<String, CartItem> cartItems = buyCart.getCartItems();
        List<CartItem> cartItem = new ArrayList<>(cartItems.values());
        List<ShopCartVo> shopCartVo =new ArrayList<ShopCartVo>();
        for (CartItem item : cartItem) {
            ShopCartVo vo = new ShopCartVo();
            vo.setItemName(item.getItemName());
            vo.setItemImgUrl(item.getItemImgUrl());
            vo.setSpecId(item.getSpecId());
            vo.setSpecName(item.getSpecName());
            vo.setPriceDiscount(item.getPriceDiscount());
            vo.setPriceNormal(item.getPriceNormal());
            vo.setBuyCounts(item.getBuyCounts());
//            vo.setTotalPrice(buyCart.getTotalPrice());
            shopCartVo.add(vo);
        }
        return JsonResult.isOk(shopCartVo);
    }

    @PostMapping("/del")
    public JsonResult delCartItemBySpecId(String userId,String itemSpecId,HttpServletRequest request, HttpServletResponse response){
        log.info("商品属性ID：" + itemSpecId);
        //根据商品属性id获取ItemSpec对象中商品的id
        String itemId = itemsService.queryItemIdByItemSpecId(itemSpecId);
        //获取Session
        HttpSession session = request.getSession();
        BuyCart buyCart = (BuyCart) session.getAttribute(shopProperties.getShopCarCookieName());
        buyCart.removeCartItem(itemId,itemSpecId);
        log.info("--------------打印Session购物车的数据"+ ReflectionToStringBuilder.toString(buyCart));
        return JsonResult.isOk();
    }
    @GetMapping("/delAll")
    public JsonResult  delBuyCart(HttpServletRequest request, HttpServletResponse response){
        HttpSession session = request.getSession();
        log.info("清空Session前购物车ID"+session.getId());
        //清空购物车，清除服务器中的Session
        BuyCart buyCart = (BuyCart) (BuyCart)session.getAttribute(shopProperties.getShopCarCookieName());
        buyCart.clearBuyCart();
        session.removeAttribute(shopProperties.getShopCarCookieName());
        log.info("清空Session后购物车ID"+session.getId());
        //清空购物车，清除浏览器中的cookie
        CookieUtils.setCookie(request,response,shopProperties.getShopCarCookieName(),"");
        log.info("--------------打印Session购物车的数据"+ ReflectionToStringBuilder.toString(buyCart));
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