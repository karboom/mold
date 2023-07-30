package me.karboom.mold;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RuleTest {
	static class Extra {
		public String name;

		public void setSubIds(List<String> subIds) {
			this.subIds = subIds;
		}

		public List<String> subIds;

		public void setName(String name) {
			this.name = name;
		}
	}

	static class UserA {
		public String id;
		public Extra extra;

		public void setId(String id) {
			this.id = id;
		}

		public void setExtra(Extra extra) {
			this.extra = extra;
		}
	}
	private List objListEmpty = List.of();
	private List<String> objListString = List.of("1", "sdf");

	private List<Integer> objListInt = List.of(1, 2);
	private List objListNull;
	private String objStringEmpty = "";
	private String objStringNumber = "1";
	private String objStringFloat1 = "1.2";
	private String objStringFloat3 = "1.102";
	private String objStringChar = "你好sdf";
	private String objStringTrim = "are you";
	private String objStringNoTrim = " 笑了 ";
	private Integer objInt0 = 0;
	private boolean objBool = true;

	private enum classEnum {
		A(), B()
	}

	@BeforeEach
	void setUp() {
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void valid() {
		var rule1 = new Rule().valid(classEnum.class);
		var res1 = rule1.verify("A");
		Assertions.assertEquals(res1.success, true);
		var res2 = rule1.verify("X");
		Assertions.assertEquals(res2.success, false);

		var rule2 = new Rule().valid(objListString);
		var res21 = rule2.verify(1);
		Assertions.assertEquals(res21.success, false);
		var res22 = rule2.verify("sdf");
		Assertions.assertEquals(res22.success, true);

		var rule3 = new Rule().valid(objListInt);
		var res31 = rule3.verify("1");
		Assertions.assertEquals(res31.success, false);
	}

	@Test
	void testObject() {
		var rule1 = new Rule().object(new HashMap<>(){{
			put("id", new Rule().string());
			put("extra", new Rule().object(new HashMap<>(){{
				put("name", new Rule().string());
				put("subIds", new Rule().array(List.of(new Rule().string())));
			}}));
		}});


		var userA = new UserA();
		userA.setId("2");
		userA.setExtra(new Extra() {{
			setName("petter");
			setSubIds(List.of("123"));
		}});

		var res11 = rule1.verify(userA);
		Assertions.assertEquals(res11.success, true);

		var rule2 = new Rule().object(new HashMap<>(){{
			put("id", new Rule().string());
			put("extra", new Rule().object(new HashMap<>(){{
				put("name", new Rule().string());
				put("subIds", new Rule().array(List.of(new Rule().bool())));
			}}));
		}});
		var res21 = rule2.verify(userA);
		Assertions.assertEquals(res21.success, false);
		Assertions.assertEquals(res21.path, "extra.subIds[0]");

		var rule3 = new Rule().object(new HashMap<>(){{
			put("id", new Rule().string());
			put("extra", new Rule().object(new HashMap<>(){{
				put("name", new Rule().array());
				put("subIds", new Rule().array());
			}}));
		}});
		var res31 = rule3.verify(userA);
		Assertions.assertEquals(res31.success, false);
		Assertions.assertEquals(res31.path, "extra.name");
	}

	@Test
	void array() {
		var res1 = new Rule().array().verify(objListEmpty);
		Assertions.assertEquals(res1.success, true);
		var res2 = new Rule().array().verify(objStringFloat1);
		Assertions.assertEquals(res2.success, false);
		var res3 = new Rule().array().verify(objListNull);
		Assertions.assertEquals(res3.success, false);


		var rule1 = new Rule().array(List.of(new Rule().string()));
		var res11 = rule1.verify(List.of(1));
		Assertions.assertEquals(res11.success, false);
		Assertions.assertEquals(res11.path, "[0]");
		var res12 = rule1.verify(List.of("xx"));
		Assertions.assertEquals(res12.success, true);

		var rule2 = new Rule().array(List.of(new Rule().bool(), new Rule().string()));
		var res21 = rule2.verify(List.of(false));
		Assertions.assertEquals(res21.success, true);
		var res22 = rule2.verify(List.of(1));
		Assertions.assertEquals(res22.success, false);
	}

	@Test
	void arrayItem() {
	}

	@Test
	void unique() {
	}

	@Test
	void ordered() {
	}

	@Test
	void sparse() {
	}

	@Test
	void bool() {
		var rule = new Rule().bool();

		var res1 = rule.verify(objBool);
		Assertions.assertEquals(res1.success, true);

		var res2 = rule.verify(objStringFloat1);
		Assertions.assertEquals(res2.success, false);
	}

	@Test
	void string() {
	}

	@Test
	void trim() {
	}

	@Test
	void number() {
	}

	@Test
	void precision() {
	}

	@Test
	void error() {
	}

	@Test
	void testError() {
	}

	@Test
	void regex() {
	}

	@Test
	void date() {
	}

	@Test
	void maxLen() {
	}

	@Test
	void minLen() {
	}

	@Test
	void maxVal() {
	}

	@Test
	void minVal() {
	}

	@Test
	void condition() {
	}

}