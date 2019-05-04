package com.suppresswarnings.corpus.service.captcha;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.suppresswarnings.corpus.common.Const;
import com.suppresswarnings.corpus.common.Context;
import com.suppresswarnings.corpus.common.State;
import com.suppresswarnings.corpus.service.CorpusService;
import com.suppresswarnings.corpus.service.WXContext;

public class CaptchaContext extends WXContext {
	public static final String CMD = "我要验证码";
	String numbers = null;
	String number = null;

	State<Context<CorpusService>> captcha = new State<Context<CorpusService>>() {

		/**
		 * 
		 */
		private static final long serialVersionUID = -8860395301948178002L;

		@Override
		public void accept(String t, Context<CorpusService> u) {
			number = t;
			String key = String.join(Const.delimiter, Const.Version.V1, "Info", "CaptchaList", number);
			String exist = u.content().account().get(key);
			if(exist == null) {
				u.output("你输入的手机号不存在，请确认：" + number);
				return;
			} else {
				String text = u.content().account().get(String.join(Const.delimiter, Const.Version.V1, "Info", "Captcha", number, "Text"));
				String stamp = u.content().account().get(String.join(Const.delimiter, Const.Version.V1, "Info", "Captcha", number, "Time"));
				long report = Long.valueOf(stamp);
				if(System.currentTimeMillis() - report > TimeUnit.MINUTES.toMillis(10)) {
					u.output("10分钟内没有收到验证码\n手机号：" + number);
					return;
				}
				SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
				u.output("当前时间：" + format.format(new Date()) + "\n更新时间：" + format.format(new Date(report)) +"\n手机号：" + number+ "\n验证码：" + text);
			}
			
			u.output("如果未收到验证码或者验证码已过期，请再次输入手机号：");
		}

		@Override
		public State<Context<CorpusService>> apply(String t, Context<CorpusService> u) {
			logger.info("[captcha] input: " + t);
			if(t.length() > 4 && Pattern.compile("\\d+").matcher(t.substring(0, 4)).matches()) {
				return captcha;
			}
			u.content().atUser(openid(), "感谢使用「素朴网联」的服务，如果再次需要验证码，请输入：" + CMD);
			return init;
		}

		@Override
		public String name() {
			return "输出验证码";
		}

		@Override
		public boolean finish() {
			return true;
		}
		
	};
	
	State<Context<CorpusService>> enter = new State<Context<CorpusService>>() {

		/**
		 * 
		 */
		private static final long serialVersionUID = 3851369518154268501L;

		@Override
		public void accept(String t, Context<CorpusService> u) {
			u.output("「素朴网联」使用须知\n" + 
					"在使用前你应该知晓该电话号码是所有人共享\n" + 
					"请不要用这个电话号码接收重要内容\n" + 
					"该电话号码只用于爱奇艺会员登录\n" + 
					"他人可以通过此电话号码找回密码，所以注册时应注意个人信息\n" + 
					"由此造成经济损失概不负责\n" + 
					"为了让大家共享，请勿修改密码\n" + 
					"在使用时即代表以上条款已同意");
			u.output("请输入手机号：13727872757");
		}

		@Override
		public State<Context<CorpusService>> apply(String t, Context<CorpusService> u) {
			if(CMD.equals(t) || t.startsWith("SCAN_")) {
				return enter;
			}
			return captcha;
		}

		@Override
		public String name() {
			return "更新验证码";
		}

		@Override
		public boolean finish() {
			return false;
		}
		
	};

	public CaptchaContext(String wxid, String openid, CorpusService ctx) {
		super(wxid, openid, ctx);
		this.state = enter;
	}

}
