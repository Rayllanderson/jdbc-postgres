package app;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import db.DB;
import entities.Order;
import entities.OrderStatus;
import entities.Product;

public class Program {

    public static void main(String[] args) throws SQLException {

	Connection conn = DB.getConnection();

	Statement st = conn.createStatement();

	String sql = "SELECT * FROM tb_order " +
		"INNER JOIN tb_order_product ON tb_order.id = tb_order_product.order_id " +
		"INNER JOIN tb_product ON tb_product.id = tb_order_product.product_id";
	
	ResultSet rs = st.executeQuery(sql);

	Map <Long, Order> map = new HashMap<>();
	Map<Long, Product> prods = new HashMap<>();

	while (rs.next()) {
	    Long orderId = rs.getLong("order_id");
	    Long prodId = rs.getLong("product_id");
	    boolean orderNotExists = map.get(orderId) == null;
	    boolean productNotExists = prods.get(prodId) == null;
	    if(orderNotExists) {
		map.put(orderId, instantiateOrder(rs));
	    }
	    if(productNotExists) {
		prods.put(prodId, instantiateProduct(rs));
	    }
	    map.get(orderId).getProducts().add(prods.get(prodId));
	}
	
	map.entrySet().forEach(x -> System.out.println(x));
	
//	products.forEach(x -> System.out.println(x));
    }

    private static Order instantiateOrder(ResultSet rs) throws SQLException {
	Order order = new Order();
	order.setId(rs.getLong("order_id"));
	order.setLatitude(rs.getDouble("latitude"));
	order.setLongitude(rs.getDouble("longitude"));
	order.setMoment(rs.getTimestamp("moment").toInstant());
	order.setStatus(OrderStatus.values()[rs.getInt("status")]);
	return order;
    }

    private static Product instantiateProduct(ResultSet rs) throws SQLException {
	Long id = rs.getLong("product_id");
	String name = rs.getString("Name");
	Double price = rs.getDouble("price");
	String description = rs.getString("description");
	String imageUri = rs.getString("image_uri");
	return new Product(id, name, price, description, imageUri);
    }
}
