package top.chris.shop.service.impl;

import lombok.extern.java.Log;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import top.chris.shop.mapper.CartMapper;
import top.chris.shop.pojo.BuyCart;
import top.chris.shop.pojo.CartItem;
import top.chris.shop.pojo.vo.CartItemVo;
import top.chris.shop.pojo.vo.ShopCartVo;
import top.chris.shop.service.CartService;

import java.util.*;

@Log
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private Sid sid;

    /**
     * 将已登录用户的购物车存储到数据库中
     * @param buyCart Session中存储的购物车对象
     * @param userId  用户的id
     * 思路： 1、去数据库中查询是否有同类商品，如果有则修改购买数量，如果没有则添加新产品
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Integer addCartInDB(BuyCart buyCart,String userId) {
        Integer result = 0;
        //获取Session中存储的购物项
        Map<String, CartItemVo> cartItems = buyCart.getCartItems();
        //构建一个迭代器，用于迭代Session中的购物车对象数据
        Iterator<Map.Entry<String, CartItemVo>> it = cartItems.entrySet().iterator();
        //获取Session中购物车Map集合存储的key值，里边存储的是每一个购物车的cartid
        Set<String> ids = cartItems.keySet();
        for (String cartId : ids) {
            //从数据库中根据购物车ID和userId，获取购物车表中的值得用户下的购物车数据
            Example example = new Example(CartItem.class);
            example.createCriteria().andEqualTo("cartId",cartId).andEqualTo("userId",userId);
            //虽然返回的List集合，但是根据cart_item数据库表的设计，只要确定了cartId和UserId就唯一确定一条数据（一条购物项）
            List<CartItem> cartItemList = cartMapper.selectByExample(example);
            //购物车没有数据，直接新添加的商品，直接存储在数据库中
            log.info("数据库的中购物车是否为空:"+String.valueOf(cartItemList == null));
            log.info("数据库中的购物车的数据："+ ReflectionToStringBuilder.toString(cartItemList));
            log.info("数据库中的购物车中有多少条数据："+ cartItemList.size());
            if (cartItemList.size() == 0){
                //根据购物车cartId，从迭代器中取出该购物项添加到数据库中，这样可以避免迭代全部数据。
                result = insertCartItemToDB(it,userId,cartId);
            }else { //数据库中有用户购物车的数据，此时需要判断用户最新添加的商品项是否已经存在购物车内，如果存在则取出后修改数量，如果不存在才进行存储
                if (cartItemList.get(0).getCartId().equals(cartId) && cartItemList.get(0).getUserId().equals(userId)){//新添加的商品项已经存在与数据库中，只需要修改原来购买数量即可
                    Integer buyCount = 0;
                    while (it.hasNext()){
                        Map.Entry<String, CartItemVo> next = it.next();
                        if (next.getKey().equals(cartId)){
                            //获取对应id在Session中存储的购买数量
                            buyCount = next.getValue().getBuyCounts();
                            break;
                        }
                    }
                    result = updateCartItemBuyCountToDB(cartId,userId,buyCount);
                }
            }
        }
        return result;
    }

    /**
     * 查询某用户下的购物车数据
     * @param userId 用户id
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
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

    /**
     * 根据购物车id删除用户指定的商品项
     * @param userId 用户id
     * @param cartId 购物车的cartID，不是购物车的主键id
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Integer delCartItemById(String userId, String cartId) {
        Example example = new Example(CartItem.class);
        example.createCriteria().andEqualTo("userId",userId).andEqualTo("cartId",cartId);
        Integer result = cartMapper.deleteByExample(example);
        return result;
    }

    /**
     * 根据用户id删除该用户购物车内的所有商品项
     * @param userId 用户id
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Integer delAllCartItems(String userId) {
        Example example = new Example(CartItem.class);
        example.createCriteria().andEqualTo("userId",userId);
        Integer result = cartMapper.deleteByExample(example);
        return result;
    }

    /**
     * 根据userId和specId查询用户购物车中的购物项信息
     * @param userId 用户id
     * @param specId 规格id
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<CartItem> queryCartByUserIdAndSpecId(String userId, String specId) {
        Example example = new Example(CartItem.class);
        example.createCriteria().andEqualTo("userId",userId).andEqualTo("specId",specId);
        List<CartItem> cartItems = cartMapper.selectByExample(example);
        return cartItems;
    }

    /**
     * 插入用户购物车的数据到数据库中
     * @param it Session中存储的购物项，转换为可迭代对象，最后通过迭代把数据写入数据库
     * @param userId 用于标记哪个用户的购物车数据
     * @param cartId 购物车的CartID
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public Integer insertCartItemToDB(Iterator<Map.Entry<String, CartItemVo>> it,String userId,String cartId){
        Integer result = 0;
        //通过迭代器把Session中存储的购物项数据遍历出来，然后写入数据库
        while (it.hasNext()){
            Map.Entry<String, CartItemVo> next = it.next();
            if (next.getKey().equals(cartId)){
                CartItem cartItem = new CartItem();
                cartItem.setId(sid.nextShort());
                cartItem.setCartId(cartId);
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
                result = cartMapper.insert(cartItem);
                break;
            }
        }
        return result;
    }

    /**
     * 修改用户购物车的数据到数据库中（添加同一类商品时，直接修改原商品在数据库中的购买数量）
     * @param cartId 已存在数据库中的商品id，目的是获取该id的购物车对象，然后把最新的购买数量重新给它赋上去
     * @param userId 用户的id
     * 以上两个参数能够在数据库中定位一条数据，即购物车中的一条购物项。
     * @param buyCount 相同商品中，得到用户最新的购买该商品的数量，目的是最新的购买数量重新赋值给数据库中对应商品的原数量
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public Integer updateCartItemBuyCountToDB(String cartId,String userId,Integer buyCount){
        //从数据库中根据购物车ID和userId，获取购物车表中的值得用户下的购物车数据
        Example example = new Example(CartItem.class);
        example.createCriteria().andEqualTo("cartId",cartId).andEqualTo("userId",userId);
        //虽然返回的List集合，但是根据cart_item数据库表的设计，只要确定了cartId和UserId就唯一确定一条数据（一条购物项）
        List<CartItem> cartItemList = cartMapper.selectByExample(example);
        //修改原商品的最新购买数量
        cartItemList.get(0).setBuyCounts( cartItemList.get(0).getBuyCounts()+buyCount);
        //重新写入数据库中
        Integer result = cartMapper.updateByPrimaryKey(cartItemList.get(0));
        return result;
    }



}
