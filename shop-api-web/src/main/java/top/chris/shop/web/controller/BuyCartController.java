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
import java.util.*;

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
    public JsonResult addCart(String itemId,String userId, Integer amount,String specId, HttpServletRequest request, HttpServletResponse response) {
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
            cartItemVo.setCartId(itemId+":"+itemsSpec.getId());
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
        log.info("刷新购物车时，用户的id是多少："+userId);
        if (!userId.equals("undefined")){
            //用户已经登录
            //将该用户存储在Session中的购物车的数据添加到数据库中
            HttpSession session = request.getSession(false);
            UsersVo user = (UsersVo) session.getAttribute("user");
            cartService.addCartInDB(buyCart,user.getId());
            log.info("用登录的用户成功把购物车数据插入的cart表中");
            //当用户已经登录时才清空掉Session中的购物车数据,避免出现数据叠加/混乱的插入/修改到数据库；用户未登录点击添加商品时，不允许删除Session中购物车，因为这个是唯一介质存储未登录状态下的购物车
            session.removeAttribute(shopProperties.getShopCarCookieName());
        }
        return JsonResult.isOk();
    }

    @GetMapping("/refresh")
    public JsonResult renderShopCart(String userId,HttpServletRequest request, HttpServletResponse response){
        log.info("刷新购物车时，用户的id是多少："+userId);
        //用户尚未登录就刷新购物车,此时展示Session中的购物车数据
        BuyCart buyCart = getBuyCart(request,response);
        if(userId.equals("undefined")){
            log.info("--------------打印未登录的Session购物车的数据"+ ReflectionToStringBuilder.toString(buyCart));
            Set<Map.Entry<String, CartItemVo>> entries = buyCart.getCartItems().entrySet();
            List<ShopCartVo> shopCartVos = new ArrayList<>();
            for (Map.Entry<String, CartItemVo> entry : entries) {
                ShopCartVo vo = new ShopCartVo();
                vo.setItemId(entry.getValue().getItemId());
                vo.setSpecId(entry.getValue().getSpecId());
                vo.setItemName(entry.getValue().getItemName());
                vo.setSpecName(entry.getValue().getSpecName());
                vo.setItemImgUrl(entry.getValue().getItemImgUrl());
                vo.setPriceNormal(entry.getValue().getPriceNormal());
                vo.setPriceDiscount(entry.getValue().getPriceDiscount());
                vo.setBuyCounts(entry.getValue().getBuyCounts());
                shopCartVos.add(vo);
            }
            return JsonResult.isOk(shopCartVos);
        }else {
            /**
             * 1、 当用在购物车页面点击登录，跳转到登录页面，登录成功后，跳转回购物车页面时，才把未登录前的添加到购物车的数据刷入数据库，之后用户处于登录状态，
             *      每次点击添加购物车按钮就会出发addCart方法，该方法先获取一个Session中的购物车，然后检测用户是否登录，登录了就把购物车的商品刷入数据库，
             *      然后删掉Session中的购物车
             * 2、 用户登录后，由于addCart方法每次都会删除掉Session中的数据，所以每次刷新购物车内容的时候，就先检测Session中购物车是否为空，一般情况下一定为空，
             *      那么刷新购物车的方法就不会再去对数据库进行增加和修改操作。[因为该方法主要目的还是当用户从未登录变为已登录时，把Session中数据刷入数据库，之后除于
             *      登录状态后，该方法就不再操作数据的增加和修改，只执行查询操作]
             */
            //用户登录了，从Session中获取用户信息
            HttpSession session = request.getSession(false);
            UsersVo user =  (UsersVo) session.getAttribute("user");;
            if (buyCart.getCartItems().size() == 0){
                //用于判断Session购物车内是否有数据，一般从未登录->已登录这个状态过程中就会存在数据，之后想要Session中有数据只能是用户点击添加商品的按钮，添加完商品到数据库后就会执行消灭Session内容，下次刷新购物车时这里就不会再执行数据库的增和修操作
                //再一次点击了刷新页面，之前的存放在Session中的数据已经清空（因为此时用户已经登录，之前的未登录的数据已经写入了数据库），此时不需要再往数据库插入数据，避免数据重复
            }else {
                //把登录前添加到Session中购物车写入数据库中
                cartService.addCartInDB(buyCart,user.getId());
                //清空Session购物车的内容
                session.removeAttribute(shopProperties.getShopCarCookieName());
            }
            //用户登录了，就直接展示它在数据库中的购物车项
            List<ShopCartVo> shopCartVos = cartService.queryCartByUserId(user.getId());
            if (shopCartVos.size() == 0){
                return JsonResult.isErr(500,"亲爱的，现在您的购物车是空的哟，请到商城主页进行购物。");
            }else {
                return JsonResult.isOk(shopCartVos);
            }
        }
    }

    @PostMapping("/del")
    public JsonResult delCartItemBySpecId(String itemSpecId,HttpServletRequest request, HttpServletResponse response){
        //根据商品属性id获取ItemSpec对象中商品的id
        String itemId = itemsService.queryItemIdByItemSpecId(itemSpecId);
        //获取Session,false代表：不创建session对象，只是从request中获取。
        HttpSession session = request.getSession(false);
        //获取登录用户的信息
        UsersVo user = (UsersVo) session.getAttribute("user");
        //构建购物车的cartId
        String cartId = itemId+":"+itemSpecId;
        //删除数据库中的对应数据（存储到数据库后，Session的数据就已经不重要了，到时候买单时根据用户传入的userId、id[itemId:specId]从数据库中获取购物车内容）
        //根据购物车id和userId从数据库中删除用户的指定的购物车商品
        Integer result = cartService.delCartItemById(user.getId(),cartId);
        log.info("删除了购物车CartId："+cartId+"的"+result+"条数据");
        return JsonResult.isOk(result);
    }
    @GetMapping("/delAll")
    public JsonResult  delBuyCart(HttpServletRequest request, HttpServletResponse response){
        //false代表：不创建session对象，只是从request中获取。
        HttpSession session = request.getSession(false);
        //获取登录用户的信息
        UsersVo user = (UsersVo) session.getAttribute("user");
        //根据用户userId从数据库中删除用户的指定的购物车商品
        Integer result = cartService.delAllCartItems(user.getId());
        //清空购物车，清除浏览器中的cookie
        CookieUtils.setCookie(request,response,shopProperties.getShopCarCookieName(),"");
        log.info("删除了用户："+user.getUsername()+"的所有购物车数据，一个删除了："+result+"条数据");
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