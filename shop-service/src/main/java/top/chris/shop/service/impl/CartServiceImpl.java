package top.chris.shop.service.impl;

import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import top.chris.shop.mapper.CartMapper;
import top.chris.shop.pojo.BuyCart;
import top.chris.shop.pojo.CartItem;
import top.chris.shop.pojo.vo.CartItemVo;
import top.chris.shop.pojo.vo.ShopCartVo;
import top.chris.shop.service.CartService;

import java.util.*;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartMapper cartMapper;

    /**
     *
     * @param buyCart
     * @param userId
     * 思路： 1、去数据库中查询是否有同类商品，如果有则修改购买数量，如果没有则添加新产品
     */
    @Override
    public void addCartInDB(BuyCart buyCart,String userId) {
        //获取Session中存储的购物项
        Map<String, CartItemVo> cartItems = buyCart.getCartItems();
        //构建一个迭代器，用于迭代Session中的购物车对象数据
        Iterator<Map.Entry<String, CartItemVo>> it = cartItems.entrySet().iterator();
        //获取Session中购物车Map集合存储的key值，里边存储的是每一个购物车的id
        Set<String> ids = cartItems.keySet();
        for (String id : ids) {
            //从数据库中根据购物车ID，获取购物车表中的数据
            CartItem cartItem = cartMapper.selectByPrimaryKey(id);
            //购物车没有数据，直接新添加的商品，直接存储在数据库中
            if (cartItem == null){
                //根据购物车ID，从迭代器中取出该购物项添加到数据库中，这样可以避免迭代全部数据。
                insertCartItemToDB(it,userId,id);
            }else { //数据库中有用户购物车的数据，此时需要判断用户最新添加的商品项是否已经存在购物车内，如果存在则取出后修改数量，如果不存在才进行存储
                if (cartItem.getId().equals(id)){//新添加的商品项已经存在与数据库中，只需要修改原来购买数量即可
                    Integer buyCount = 0;
                    while (it.hasNext()){
                        Map.Entry<String, CartItemVo> next = it.next();
                        if (next.getKey().equals(id)){
                            //获取对应id在Session中存储的购买数量
                            buyCount = next.getValue().getBuyCounts();
                            break;
                        }
                    }
                    updateCartItemBuyCountToDB(id,buyCount);
                }
            }
        }
    }

    @Override
    public List<ShopCartVo> queryCartByUserId(String userId) {
        Example example = new Example(CartItem.class);
        example.createCriteria().andEqualTo("userId",userId);
        List<CartItem> cartItems = cartMapper.selectByExample(example);
        List<ShopCartVo> shopCartVos = new ArrayList<ShopCartVo>();
        for (CartItem cartItem : cartItems) {
            ShopCartVo vo = new ShopCartVo();
            vo.setItemId(cartItem.getItemId());
            vo.setSpecId(cartItem.getSpecId());
            vo.setItemName(cartItem.getItemName());
            vo.setSpecName(cartItem.getSpecName());
            vo.setItemImgUrl(cartItem.getItemImgUrl());
            vo.setPriceNormal(cartItem.getPriceNormal());
            vo.setPriceDiscount(cartItem.getPriceDiscount());
            vo.setBuyCounts(cartItem.getBuyCounts());
            shopCartVos.add(vo);
        }
        return shopCartVos;
    }

    @Override
    public void delCartItemById(String userId, String id) {
        Example example = new Example(CartItem.class);
        example.createCriteria().andEqualTo("userId",userId).andEqualTo("id",id);
        cartMapper.deleteByExample(example);
    }

    @Override
    public void delAllCartItems(String userId) {
        Example example = new Example(CartItem.class);
        example.createCriteria().andEqualTo("userId",userId);
        cartMapper.deleteByExample(example);
    }

    /**
     * 插入用户购物车的数据到数据库中
     * @param it Session中存储的购物项，转换为可迭代对象，最后通过迭代把数据写入数据库
     * @param userId 用于标记哪个用户的购物车数据
     * @param userId 用于标记哪个用户的购物车数据
     */
    public void insertCartItemToDB(Iterator<Map.Entry<String, CartItemVo>> it,String userId,String id){
        //通过迭代器把Session中存储的购物项数据遍历出来，然后写入数据库
        while (it.hasNext()){
            Map.Entry<String, CartItemVo> next = it.next();
            if (next.getKey().equals(id)){
                CartItem cartItem = new CartItem();
                cartItem.setId(next.getValue().getItemId()+":"+next.getValue().getSpecId());
                cartItem.setUserId(userId);
                cartItem.setItemId(next.getValue().getItemId());
                cartItem.setItemName(next.getValue().getItemName());
                cartItem.setItemImgUrl(next.getValue().getItemImgUrl());
                cartItem.setSpecId(next.getValue().getSpecId());
                cartItem.setSpecName(next.getValue().getSpecName());
                cartItem.setPriceNormal(next.getValue().getPriceNormal());
                cartItem.setPriceDiscount(next.getValue().getPriceDiscount());
                cartItem.setBuyCounts(next.getValue().getBuyCounts());
                //插入到cart数据表中
                cartMapper.insert(cartItem);
                break;
            }
        }
    }

    /**
     * 修改用户购物车的数据到数据库中（添加同一类商品时，直接修改原商品在数据库中的购买数量）
     * @param id 已存在数据库中的商品id，目的是获取该id的购物车对象，然后把最新的购买数量重新给它赋上去
     * @param buyCount 相同商品中，得到用户最新的购买该商品的数量，目的是最新的购买数量重新赋值给数据库中对应商品的原数量
     */
    public void updateCartItemBuyCountToDB(String id,Integer buyCount){
        //获取存储在数据库中的原购物商品数据
        CartItem cartItem = cartMapper.selectByPrimaryKey(id);
        //修改原商品的最新购买数量
        cartItem.setBuyCounts(buyCount);
        //重新写入数据库中
        cartMapper.updateByPrimaryKey(cartItem);
    }
}
