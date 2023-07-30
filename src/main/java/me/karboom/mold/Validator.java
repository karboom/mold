package me.karboom.mold;

import lombok.Data;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public class Validator {
	public static Rule object(HashMap<String, Rule> object) {
		return new Rule().object(object);
	}

	public static Rule array(List<Rule> input) {return new Rule().array(input);}

	public static Rule valid(Object input) {
		return new Rule().valid(input);
	}

	public static Rule regex(String input) {
		return new Rule().regex(input);
	}

	public static Rule date() {
		return new Rule().date();
	}

}
