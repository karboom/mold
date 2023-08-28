package me.karboom.mold;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 每一个函数对应一个私有变量，互斥之类的，配置的的时候决定
 */
public class Rule {

	interface CustomRuleFunc extends BiFunction<Object, Object, Rule> {}
	interface CustomResultFunc extends BiFunction<Object, Object, Boolean> {}

	private Map<Result.ERROR.KEY, String> errorTpl = Result.ERROR.TPL;

	Rule(Map<Result.ERROR.KEY, String> errorTpl) {
		this.errorTpl = errorTpl;
	}
	Rule(){}

	private Object _valid;

	// 标记X的变量互斥，设置的时候，就清空另外的
	private HashMap<String, Rule> _object;
	private String _regex;
	private List<Rule> _array;

	private Consumer<Result> _error;

	private Boolean _bool;
	private Boolean _string;
	private Integer _precision;
	private Boolean _number;

	private Boolean _trim;
	private Boolean _upperCase;
	private Boolean _lowerCase;

	private Integer _maxLen;
	private Integer _minLen;
	private String _maxVal;
	private String _minVal;

	private CustomRuleFunc _customRule;
	private CustomResultFunc _customResult;
	/**
	 * 只放行允许的值
	 */
	public Rule valid(Object input) {
		_valid = input;
		return this;
	}

	public Rule object(HashMap<String, Rule> object) {
		this._object = object;
		return this;
	}

	/**
	 * 满足其中一个规则
	 */
	public Rule array(List<Rule> array) {
		this._array = array;
		return this;
	}
	/**
	 * 判定数组类型
	 * @return
	 */
	public Rule array() {
		this._array = List.of();
		return this;
	}
	public Rule unique(){
		return this;
	}
	public Rule ordered(){
		return this;
	}
	public Rule sparse(){
		return this;
	}

	/**
	 * 判断是否为布尔
	 * @return
	 */
	public Rule bool() {
		this._bool = true;
		return this;
	}

	public Rule string() {
		this._string = true;
		return this;
	}

	public Rule trim() {
		this._trim = true;
		return this;
	}

	/**
	 * 数字类型
	 * @return
	 */
	public Rule number() {
		this._number = true;
		return this;
	}

	/**
	 * 精度
	 * @return
	 */
	public Rule precision(Integer number) {
		this._precision = number;
		return this;
	}

	public Rule error(String str) {
		this._error = (Result res) -> {
			res.setMessage(str);
		};
		return this;
	}

	public Rule error(Consumer<Result> func) {
		this._error = func;
		return this;
	}

	/**
	 * 正则判断
	 * @param input
	 * @return
	 */
	public Rule regex(String input) {
		this._regex = input;
		return this;
	}

	/**
	 * 判断日期格式
	 * @return
	 */
	public Rule date() {
		return regex("^\\d{4}-\\d{2}-\\d{2}(\\s\\d{2}:\\d{2}:\\d{2})?$");
	}

	/**
	 * 最大长度限制
	 * @return
	 */
	public Rule maxLen(Integer input) {
		this._maxLen = input;
		return this;
	}

	/**
	 * 最小长度限制
	 * @return
	 */
	public Rule minLen(Integer input) {
		this._minLen = input;
		return this;
	}

	/**
	 * 最大数值限制
	 */
	public Rule maxVal(String input) {
		this._maxVal = input;
		return this;}

	/**
	 * 最小数值限制
	 */
	public Rule minVal(String input) {
		this._minVal = input;
		return this;}

	/**
	 * 自定义条件
	 * @param func
	 * @return
	 */
	public Rule custom(CustomRuleFunc func) {
		this._customRule = func;
		this._customResult = null;
		return this;
	}

	public Rule custom(CustomResultFunc func) {
		this._customResult = func;
		this._customRule = null;
		return this;
	}


	// Todo 如果输入的对象，不是规则支持的类型，应该放过还是卡死
	public Result _validate(Object target, Object rootTarget) {
		// 基本类型判断
		if (_array != null) {
			if (!(target instanceof List))	{
				return this._throw(errorTpl.get(Result.ERROR.KEY.TYPE_NOT_MATCH).formatted("List"), "");
			}

			// 判断内容
			if (_array.size() > 0) {
				var items = (List<Object>) target;
				for (var i = 0; i < items.size(); i++) {
					var item = items.get(i);
					var match = false;

					for (var rule: _array) {
						var res = rule._validate(item, rootTarget);
						if (res.success) {
							match = true;
						}
					}

					if (!match) {
						return _throw("", "[%s]".formatted(i));
					}
				}
			}

		}
		if (_bool != null) {
			if (!(target instanceof Boolean)) {
				return this._throw("", "");
			}
		}
		if (_string != null) {
			if (!(target instanceof String)) {
				return this._throw("", "");
			}
		}


		if (_object != null) {
			// 兼容匿名类
			var clz = target.getClass();
			if (clz.isAnonymousClass()) clz = clz.getSuperclass();

			Field[] fields = clz.getDeclaredFields();

			// Todo 长度不一致如何判定
			if (fields.length != _object.keySet().size()) {
				return _throw("x", "");
			}

			for (String key: _object.keySet()) {
				var rule = _object.get(key);
				Object value = null;

				// 遍历字段并将它们存储在HashMap中
				for (Field field : fields) {
					field.setAccessible(true); // 设置字段可访问

					try {
						// 获取字段的名称和值，并存储在HashMap中
						String fieldName = field.getName();
						if (fieldName.equals(key)) {
							value = field.get(target);
						}
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}

				// 如果预期的字段没找到，直接报错
				if (value == null) {
					return this._throw("字段没有找到", "");
				}

				var res = rule._validate(value, rootTarget);
				if (!res.success) {
					var path = key;
					if (!res.path.equals("")) {
						if (res.path.startsWith("[")) {
							path = path + res.path;
						} else {
							path = path + "." + res.path;
						}
					}
					return _throw(res.message, path);
				}
			}
		}

		if (_valid != null) {
			if (_valid instanceof List) {
				if (!((List<?>) _valid).contains(target)) {
					return this._throw("", "");
				}
			} else if (_valid instanceof Class && ((Class) _valid).isEnum()){
				// 处理枚举逻辑
				var match = false;
				for (var field : ((Class<?>) _valid).getDeclaredFields()) {
					if (field.isEnumConstant()) {
						if (field.getName().toString().equals((String) target)) {
							match = true;
						}
					}
				}

				if (!match) {
					return _throw("", "");
				}
			} else {
				throw new RuntimeException("valid只允许List、Enum");
			}
		}

		if (_regex != null) {
			if (target instanceof String) {
				var res = ((String) target).matches(_regex);
				if (!res) {
					return _throw("", "");
				}
			} else {
				return _throw("", "");
			}
		}

		if (_maxLen != null) {
			if (target instanceof String value) {
				if (value.length() > _maxLen) {
					return _throw("", "");
				}
			}
		}
		if (_minLen != null) {
			if (target instanceof String value) {
				if (value.length() < _minLen) {
					return _throw("", "");
				}
			}
		}

		if (_maxVal != null) {
			// Todo 转换为字符串，然后对比
		}
		if (_minVal != null) {
		}

		if (_trim != null) {
			if (target instanceof String value) {
				if (value.trim().length() != value.length()) {
					return _throw("", "");
				}
			}
		}

		if (_precision != null) {
			if (target instanceof String value) {

			}
			if (target instanceof Double value) {

			}
			if (target instanceof BigDecimal) {
				// Todo 支持decimal
			}
		}


		if (_customRule != null) {
			// Todo 这种条件下不让返回的Rule throw
			var res = _customRule.apply(rootTarget, target).verify(target);
			if (!res.success) {
				return _throw("", "");
			}
		}
		if (_customResult != null) {
			var res = _customResult.apply(rootTarget, target);
			if (!res) {
				return _throw("", "");
			}
		}



		return new Result(){{
			setSuccess(true);
		}};
	}
	private Result _validate(Object target) {
		// Todo 深度复制
		var rootTarget = ObjectUtil.clone(target);
		return _validate(target, target);
	}

	private Result _throw(String message, String path) {
		var result = new Result();
		result.setPath(path);
		result.setMessage(message);

		if (_error != null) {
			this._error.accept(result);
		}

		return result;
	}
	public Result verify(Object target) {
		var res = _validate(target);
		return res;
	}
}
