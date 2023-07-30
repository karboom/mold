package me.karboom.mold;


import java.util.HashMap;
import java.util.Map;

public class Result  extends RuntimeException{
	public static class ERROR {
		public enum KEY {
			TYPE_NOT_MATCH();
		}
		public static Map<KEY, String> TPL = new HashMap<KEY, String> (){{
			put(KEY.TYPE_NOT_MATCH, "只允许%s");
		}};
	}

	public Boolean success = false;
	public String message = "";

	public String path;

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}


	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}


	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}