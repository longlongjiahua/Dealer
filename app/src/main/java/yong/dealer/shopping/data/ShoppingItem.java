/*
   [{"category":"meat","foods":[{"name":"back bacon:","nutrition":{"calories":"287kcal","carbohydrate":"0.1g","protein":"23.2g","fat":"21.6g","fibre":"0.0g"}},{"name":"sausage:","nutrition":{"calories":"252.0kcal","carbohydrate":"7.0g","protein":"14.5g","fat":"18.5g","fibre":"0.6g"}},{"name":"chicken breast:","nutrition":{"calor
 */
package yong.dealer.shopping.data;

import java.util.ArrayList;
import java.util.List;
/*
[{"category":"meat","food":[{"name":"back bacon:","nutrition":{"calories":"287kcal","carbohydrate":"0.1g","protein":"23.2g","fat":"21.6g","fibre":"0.0g"}},{"name":"sausage:","nutrition":{"calories":"252.0kcal","carbohydrate":"7.0g","protein":"14.5g","fat":"18.5g","fibre":"0.6g"}},{"name":"chicken breast:","nutrition":{"calories":"157.0kcal","carbohydrate":"0.2g","protein":"23.9g","fat":"6.7g","fibre":"0.1g"}},{"name":"breast fillet:","nutrition":{"calories":"145.8kcal","carbohydrate":"0.1g","protein":"24.8g","fat":"4.6g","fibre":"0.2g"}},{"name":"fillet steak:","nutrition":{"calories":"191.3kcal","carbohydrate":"0.0g","protein":"28.6g","fat":"8.5g","fibre":"0.0g"}},{"name":"gravy:","nutrition":{"calories":"56.4kcal","carbohydrate":"4.5g","protein":"2.4g","fat":"3.2g","fibre":"0.6g"}},{"name":"ham:","nutrition":{"calories":"115.0kcal","carbohydrate":"0.8g","protein":"21.8g","fat":"2.8g","fibre":"0.0g"}},{"name":"kebab:","nutrition":{"calories":"282.0kcal","carbohydrate":"4.9g","protein":"16.1g","fat":"22g","fibre":"1.8g"}},{"name":"lamb chop:","nut
 */
public class ShoppingItem {
	public String category;
	public List<Food> food=new ArrayList<Food>();

	public static class  Food {
		public String name;
		public Nutrition nutrition;
	}

	 public class Nutrition {
		public String calories;
		public String carbohydrate;
		public String protein;
		public String fat;
		public String fibre;
	}
}
