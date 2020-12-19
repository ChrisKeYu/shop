package top.chris.shop.service;

import top.chris.shop.pojo.BuyCart;
import top.chris.shop.pojo.vo.ShopCartVo;

import java.util.List;

public interface CartService {
    //将已登录用户的购物车存储到数据库中
    Integer addCartInDB(BuyCart buyCart,String userId);
    //查询某用户下的购物车数据
    List<ShopCartVo> queryCartByUserId(String userId);
    //根据购物车id删除用户指定的商品项
    Integer delCartItemById(String userId,String id);
    //根据用户id删除该用户购物车内的所有商品项
    Integer delAllCartItems(String userId);
    //根据购物车id，修改指定商品的购买数量
    Integer updateCartItemBuyCountToDB(String cartId,String userId,Integer buyCount);
}
