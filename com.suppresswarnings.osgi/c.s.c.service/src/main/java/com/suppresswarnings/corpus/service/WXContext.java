/**
 * 
 *       # # $
 *       #   #
 *       # # #
 * 
 *  SuppressWarnings
 * 
 */
package com.suppresswarnings.corpus.service;

import java.util.Set;

import com.suppresswarnings.corpus.common.CheckUtil;
import com.suppresswarnings.corpus.common.Const;
import com.suppresswarnings.corpus.common.Context;
import com.suppresswarnings.corpus.common.ContextFactory;
import com.suppresswarnings.corpus.common.State;


public class WXContext extends Context<CorpusService> {
	public static final String exit = "exit()";
	String openid;
	String wxid;
	public State<Context<CorpusService>> init = new State<Context<CorpusService>>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4836433217450201449L;

		@Override
		public void accept(String t, Context<CorpusService> u) {
			String nameKey = String.join(Const.delimiter, Const.Version.V1, openid(), "Name");
			String name = u.content().account().get(nameKey);
			Set<String> commands = u.content().factories.keySet();
			if(commands.size() < 1) {
				u.output(name + " 你好，我现在还没有准备好！");
			} else {
				if(name != null) {
					u.appendLine(name + " 你好，");
				} else {
					logger.error("get null from Account by key: " + nameKey);
					u.appendLine("还不知怎么称呼您，(输入'我要注册')");
				}
				u.appendLine("我现在只会这些操作").appendLine(commands.toString());
			}
		}

		@Override
		public State<Context<CorpusService>> apply(String t, Context<CorpusService> u) {
			String command = CheckUtil.cleanStr(t.trim()).toLowerCase();
			ContextFactory<CorpusService> cf = u.content().factories.get(command);
			if(cf == null) {
				String nowCommandKey = String.join(Const.delimiter, "Setting", "Global", "Command", command);
				String exchange = u.content().account().get(nowCommandKey);
				if(exchange != null) {
					cf = u.content().factories.get(exchange);
				}
			}
			
			if(cf != null) {
				Context<CorpusService> context = cf.getInstance(wxid(), openid(), u.content());
				if(cf.ttl() != ContextFactory.forever) {
					u.content().contextx(openid(), context, cf.ttl());
				}
				return context.state();
			}
		
			return this;
		}

		@Override
		public String name() {
			return "微信上下文初始化状态";
		}

		@Override
		public boolean finish() {
			return false;
		}
	};
	
	public State<Context<CorpusService>> set(State<Context<CorpusService>> start) {
		return new State<Context<CorpusService>>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -2352514490121930101L;

			@Override
			public void accept(String t, Context<CorpusService> u) {
				start.accept(t, u);
			}

			@Override
			public State<Context<CorpusService>> apply(String t, Context<CorpusService> u) {
				return start.apply(t, u);
			}

			@Override
			public String name() {
				return "set: " + start.name();
			}

			@Override
			public boolean finish() {
				return false;
			}
			
		};
	}
	public WXContext(String wxid, String openid, CorpusService ctx) {
		super(ctx);
		this.wxid = wxid;
		this.openid = openid;
		this.state = init;
	}

	public String openid(){
		return openid;
	}
	public String wxid() {
		return wxid;
	}
	public boolean yes(String input, String expect) {
		return confirm(input, expect, "yes y ok okay alright 好 好的 可以 嗯 是 是的 没错 当然 好啊 是啊 可以的 对 确定 确认 ");
	}
	public boolean exit(String input, String expect) {
		return confirm(input, expect, "我要退出 退出 quit exit exit() 不玩了 不想玩了 不做了 不要了 不了 算了 不用了 返回 ");
	}
	public boolean confirm(String input, String expect, String common) {
		if(expect == input) return true;
		if(input == null) return false;
		if(expect != null && expect.equals(input)) return true;
		//TODO ner check yes
		String yes = CheckUtil.cleanStr(input.trim()) + " ";
		if(common.contains(yes.toLowerCase())) return true;
		return false;
	}
}