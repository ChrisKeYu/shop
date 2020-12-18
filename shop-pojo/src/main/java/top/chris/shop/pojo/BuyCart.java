package top.chris.shop.pojo;

import lombok.Data;
import lombok.extern.java.Log;
import top.chris.shop.pojo.vo.CartItemVo;

import java.util.*;

/**
 * 购物车类，用于存储购物项
 */
@Log
@Data
public class BuyCart {
    /**
     * 用于存储购物项数据
     * 键 = 商品Id
     * 值 = 购物项
     */
    private Map<String, CartItemVo> cartItems = new HashMap<>();

    //private List<CartItem> cartItemsTest = new ArrayList<CartItem>();
    /**
     * 用于统计所有商品总价
     */
    private Integer totalPrice = 0;

    /**
     * 将一个商品添加到购物车中
     * @param newCartItemVo
     */
    public void addCartItem(CartItemVo newCartItemVo){
        String specId = newCartItemVo.getSpecId();
        String itemId = newCartItemVo.getItemId();
        String id = itemId+":"+specId;
        //判断购物车[集合]中是否有相同的商品被添加进来:判断依据(商品ID和商品类型ID组合称为 id)
        if (cartItems.containsKey(id)){
            //存在：即加入了相同商品，此时把原购物车中该商品取出来
            CartItemVo cartItemVo = cartItems.get(id);
            //改变原来的数量（取出这个商品原来在购物车的数量，然后和刚刚添加进来的同样商品的数量进行累加得到该商品最新的购买数量）
            cartItemVo.setBuyCounts(cartItemVo.getBuyCounts()+newCartItemVo.getBuyCounts());
        }else {
            //添加进来的商品是原购物车中没有的商品，此时只需要往购物车中添加此商品即可
            cartItems.put(id,newCartItemVo);
        }
        //最后修改购物车的总价
        totalPrice += newCartItemVo.getCartItemSubTotal();
    }

    /**
     * 根据商品ID来从购物车中删除数据
     * @param itemId
     */
    public void removeCartItem(String itemId,String specId){
        String id = itemId+":"+specId;;
        //根据ItemId来删除购物车中的商品项
        CartItemVo removeItemVo = cartItems.remove(id);
        //修改购物车的总价格
        totalPrice -= removeItemVo.getCartItemSubTotal();
    }

    /**
     * 清除购物车的数据
     */
    public void clearBuyCart(){
        //清空购物车
        cartItems.clear();
        //设置购物车总价格
        totalPrice = 0;
    }


}
