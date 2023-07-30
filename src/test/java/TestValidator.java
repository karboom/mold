import me.karboom.Main;
import me.karboom.mold.Validator;

import java.util.HashMap;
import java.util.List;

public class TestValidator {
	public static void main(String[] args) {
//		var validRule = Validator.valid(List.of(1, "3")).error("类型错误");
//
//		assert validRule.verify(1).success : "valid";
//		assert validRule.verify("3").success : "valid";
//		assert !validRule.verify("4").success : "valid";
//
//		var validRuleEnum = Validator.valid(Main.TYPE.class).error("");
//		assert validRuleEnum.verify("A").success : "valid";
//		assert !validRuleEnum.verify("C").success : "valid";
//
//
//		var dateRule = Validator.date();
//		assert !dateRule.verify("20").success : "should error";
//		assert dateRule.verify("2022-10-10").success: "should success";
//		assert dateRule.verify("2022-10-10 11:22:11").success: "should success";
//		assert !dateRule.verify("2022-10-10 11-22:11").success: "should error";
//
//		var objRule = Validator.object(new HashMap<>() {{
//			put("user_name", Validator.valid(List.of("A", "B")).error("用户名错误"));
//			put("2", Validator.object(new HashMap<>() {{
//				put("user_id", Validator.valid(""));
//			}}));
//		}});
	}

}
