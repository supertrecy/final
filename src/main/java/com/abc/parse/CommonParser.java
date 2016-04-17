
package com.abc.parse;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abc.db.entity.NewsInfo;
import com.abc.util.URLUtil;

/**
 * http://citybank.jrj.com.cn/2014/05/13071517197394-1.shtml
 * http://www.akxw.cn/finance/fund/ipodc/99870.html 匹配不出source
 * 
 * @author hjy
 *
 */
public class CommonParser extends NewsParser {
	public static final Logger LOG = LoggerFactory.getLogger(CommonParser.class);

	/** Used to extract base information */
	private static final String titleRegex = "<title.*?>([^<]+)</title>";
	private static final String keywordsRegex = "<meta.*?name=\"?keywords\"?.*?content=\"?(.*?)[\"/]";
	private static final String sourceRegex = "(?:来源|来源于|稿源|摘自)[：:\\s]\\s*?(?:<.*?>)+([^<>\\s]+)<";
	private static final String sourceRegex2 = "(?:来源|来自)[：:\\s]\\s*?(.*?)\\s*?[<&\\)）]"; // 纯文字
	private static final String sourceRegex3 = "<meta name=\"source\" content=\"(.*?)\">"; // 央视网

	private static Pattern pTitle;
	private static Pattern pKeywords;
	private static Pattern pSource;
	private static Pattern pSource2;
	private static Pattern pSource3;

	private static Map<String, String> siteMap = new HashMap<String, String>();

	static {
		pTitle = Pattern.compile(titleRegex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		pKeywords = Pattern.compile(keywordsRegex, Pattern.CASE_INSENSITIVE);
		pSource = Pattern.compile(sourceRegex, Pattern.CASE_INSENSITIVE);
		pSource2 = Pattern.compile(sourceRegex2, Pattern.CASE_INSENSITIVE);
		pSource3 = Pattern.compile(sourceRegex3, Pattern.CASE_INSENSITIVE);

		siteMap.put("10jqka.com.cn", "同花顺");
		siteMap.put("16hour.cn", "16小时网");
		siteMap.put("202.123.110.3", "中央政府门户网站");
		siteMap.put("21cn.com", "21CN");
		siteMap.put("80end.cn", "芭厘时尚网");
		siteMap.put("ahyouth.com", "安青网");
		siteMap.put("akxw.cn", "安康新网");
		siteMap.put("artsbj.com", "北京文艺网");
		siteMap.put("bjd.com.cn", "京报网");
		siteMap.put("bz518.com", "巴中热线");
		siteMap.put("caijing.com.cn", "财经网");
		siteMap.put("cbg.cn", "视界网");
		siteMap.put("ccaonline.cn", "中国民用航空网");
		siteMap.put("cceebb.com", "中国银企在线");
		siteMap.put("cctime.com", "飞象网");
		siteMap.put("cdrb.com.cn", "成都日报");
		siteMap.put("chinabgao.com", "中国报告大厅");
		siteMap.put("chinatibetnews.com", "西藏日报");
		siteMap.put("chisa.edu.cn", "神舟学人");
		siteMap.put("cjn.cn", "长江网");
		siteMap.put("cmt.com.cn", "医学论坛网");
		siteMap.put("cn0917.com", "宝鸡网");
		siteMap.put("cnfol.com", "中金在线");
		siteMap.put("cnky.net", "中国考研网");
		siteMap.put("cnlaw.net", "中国法网");
		siteMap.put("cnstock.com", "中国证券网");
		siteMap.put("cnstock.com", "中国旅游新闻网");
		siteMap.put("cnxz.com.cn", "中国徐州网");
		siteMap.put("court.gov.cn", "中华人民共和国最高人民法院");
		siteMap.put("cqn.com.cn", "中国质量新闻网");
		siteMap.put("eastmoney.com", "东方财富网");
		siteMap.put("eeo.com.cn", "经济观察网");
		siteMap.put("fdi.gov.cn", "中国投资指南网");
		siteMap.put("gaotie.cn", "高铁网");
		siteMap.put("glomoney.net", "广源金");
		siteMap.put("gog.com.cn", "贵州日报");
		siteMap.put("gxnews.com.cn", "南国早报");
		siteMap.put("gzgov.gov.cn", "贵州省人民政府门户网站");
		siteMap.put("hangzhou.com.cn", "杭州日报");
		siteMap.put("hbjjrb.com", "河北经济网");
		siteMap.put("henan.gov.cn", "河南省人民政府门户网站");
		siteMap.put("howbuy.com", "好买基金网");
		siteMap.put("huanbohainews.com.cn", "环渤海新闻网");
		siteMap.put("huaxia.com", "华夏经纬网");
		siteMap.put("huimengwang.com", "惠蒙网");
		siteMap.put("joyinmoney.com", "智慧财富");
		siteMap.put("reuters.com", "路透中文网");
		siteMap.put("cz001.com.cn", "中国常州网");
		siteMap.put("hsw.cn", "华商网");
		siteMap.put("mbahome.com", "中国MBAhome网");
		siteMap.put("mba.org.cn", "中国MBA网");
		siteMap.put("most.gov.cn", "中华人民共和国科学技术部");
		siteMap.put("msxh.com", "眉山全搜索");
		siteMap.put("online.sh.cn", "上海热线");
		siteMap.put("peixunwang.com.cn", "东南教育网");
		siteMap.put("dayoo.com", "广州日报");
		siteMap.put("enorth.com.cn", "北方网");
		siteMap.put("dbw.cn", "东北网");
		siteMap.put("sdnews.com.cn", "鲁网");
		siteMap.put("qz828.com", "衢州新闻网");
		siteMap.put("cnnb.com.cn", "中国宁波网");
		siteMap.put("ahyx.cc", "岳西新闻网");
		siteMap.put("chinacqsb.com", "重庆商报");
		siteMap.put("cntv.cn", "中国网络电视台");
		siteMap.put("eastday.com", "东方网");
		siteMap.put("hf365.com", "合肥在线");
		siteMap.put("qianlong.com", "千龙网");
		siteMap.put("workercn.cn", "中工网");
		siteMap.put("anhuinews.com", "中安教育网");
		siteMap.put("gansudaily.com.cn", "每日甘肃网");
		siteMap.put("guancha.cn", "观察者网");
		siteMap.put("gushi360.com", "股市360股票网");
		siteMap.put("gywb.cn", "贵阳网");
		siteMap.put("gyxww.cn", "广元新闻网");
		siteMap.put("cankaoxiaoxi.com", "参考消息网");
		siteMap.put("ijntv.cn", "济南网络广播电视台");
		siteMap.put("nmgnews.com.cn", "内蒙古新闻网");
		siteMap.put("beelink.com", "百灵网");
		siteMap.put("china.com", "中华网");
		siteMap.put("cfi.cn", "中财网");
		siteMap.put("jzrb.com", "焦作网");
		siteMap.put("china.com.cn", "中国网");
		siteMap.put("chinastock.com.cn", "中国银河证券");
		siteMap.put("bzxhw.com", "巴中讯");
		siteMap.put("chengdu.cn", "成都商报");
		siteMap.put("chinadaily.com.cn", "中国日报网");
		siteMap.put("chsi.com.cn", "学信网");
		siteMap.put("hilizi.com", "海力网");
		siteMap.put("huagu.com", "华股财经");
		siteMap.put("jwb.com.cn", "今晚网");
		siteMap.put("nbd.com.cn", "每经网");
		siteMap.put("onlylady.com", "OnlyLady");
		siteMap.put("qlwb.com.cn", "齐鲁晚报网");
		siteMap.put("sciencenet.cn", "科学网");
		siteMap.put("sxsm.com.cn", "神木新闻网");
		siteMap.put("tj.gov.cn", "天津政务网");
		siteMap.put("xhgmw.org", "辛亥革命网");
		siteMap.put("cri.cn", "CIBN");
		siteMap.put("jyb.cn", "中国教育新闻网");
		siteMap.put("nmgcb.com.cn", "内蒙古晨网");
		siteMap.put("qnr.cn", "青年人网");
		siteMap.put("sport.gov.cn", "国家体育总局");
		siteMap.put("stcn.com", "证券时报网");
		siteMap.put("taihainet.com", "台海网");
		siteMap.put("takefoto.cn", "北京晚报");
		siteMap.put("tibet3.com", "青海日报");
		siteMap.put("tobaccochina.com", "烟草在线");
		siteMap.put("wccdaily.com.cn", "华西都市报");
		siteMap.put("wuhunews.cn", "芜湖新闻网");
		siteMap.put("www.edu.cn", "中国教育和科研计算机网");
		siteMap.put("xdf.cn", "新东方");
		siteMap.put("xwh.cn", "新文化网");
		siteMap.put("yninfo.com", "云南信息港");
		siteMap.put("zgswcn.com", "中国商网");
		siteMap.put("zhuayoukong.com", "爪游控");
	}

	@Override
	public NewsInfo getParse(String content, String encoding, String url) {
		NewsInfo info = new NewsInfo();
		String contentStr = content;
		String curTime = df.format(System.currentTimeMillis());
		info.setFetchtime(curTime);
		info.setUrl(url);
		getBaseInfo(info, url, contentStr);
		return info;
	}

	/**
	 * 提取基本信息
	 * 
	 * @param info
	 * @param content
	 */
	private void getBaseInfo(NewsInfo info, String url, String content) {
		String site = "";
		String title = "";
		String pubtime = "";
		String keywords = "";
		String source = "";

		/*** 提取标题信息 ***/
		Matcher matcher = pTitle.matcher(content);
		if (matcher.find()) {
			String rowtitle = matcher.group(1).trim();
			title = rowtitle.replaceAll("\n", ""); // 标题占据多行时，将其变换为一行
			// System.out.println(rowtitle);
			int index = title.indexOf("-");
			if (index != -1)
				title = title.substring(0, index).trim();
			index = title.indexOf("_");
			if (index != -1)
				title = title.substring(0, index).trim();
			index = title.indexOf("―");
			if (index != -1)
				title = title.substring(0, index).trim();
			index = title.indexOf("|");
			if (index != -1)
				title = title.substring(0, index).trim();

			/*** 提取站点信息 ***/
			String domain = "";
			try {
				domain = URLUtil.getDomainName(url);
			} catch (Exception e) {
			}
			if (siteMap.containsKey(domain)) {
				site = siteMap.get(domain);
			} else { // 从标题中提取site
				int index2 = rowtitle.lastIndexOf("-"); // 标识1 短横线
				if (index2 != -1) {
					site = rowtitle.substring(index2 + 1, rowtitle.length()).trim();
				} else {
					index2 = rowtitle.lastIndexOf("_"); // 标识2
					if (index2 != -1) {
						site = rowtitle.substring(index2 + 1, rowtitle.length()).trim();
					}
				}
				index2 = rowtitle.lastIndexOf("―"); // 标识3 长横线
				if (index2 != -1) {
					site = rowtitle.substring(index2 + 1, rowtitle.length()).trim();
				}
				index2 = site.lastIndexOf(" ");
				if (index2 != -1) {
					site = site.substring(index2 + 1, site.length()).trim();
				}

				site = site.replaceAll("\\(.*?\\)", "");

				if (site.length() > 15) {
					site = "";
				}
				if(!site.contains("网"))
					site = "";
			}
			if ("".equals(site)) {
				site = domain;
			}
		}

		/*** 提取关键词信息 ***/
		matcher = pKeywords.matcher(content);
		if (matcher.find()) {
			keywords = matcher.group(1).trim();
		} else {
			keywords = "";
		}

		/*** 提取新闻来源信息 ***/
		String originalSourceText = "";
		matcher = pSource.matcher(content);
		if (matcher.find()) {
			source = matcher.group(1).trim();

			originalSourceText = matcher.group(0).trim();

		} else {
			matcher = pSource2.matcher(content);
			if (matcher.find()) {
				source = matcher.group(1).trim();
				originalSourceText = matcher.group(0).trim();
			} else {
				matcher = pSource3.matcher(content);
				if (matcher.find()) {
					source = matcher.group(1).trim();
					originalSourceText = matcher.group(0).trim();
				}
				else{
					originalSourceText = "";
				}
			}
		}
		/*** 提取新闻来源url信息 ***/
		info.setSourceUrl(this.extractSourceUrl(originalSourceText));
		
//		if(info.getSourceUrl().contains("weixin")){
//			System.out.println(originalSourceText);
//			System.out.println(info.getSourceUrl());
//			System.out.println("----------------------------------------------------------");
//			
//		}

		// 全角空格通过trim()去除不了，也无法通过source.indexOf(" ")找到
		if (source != null) {
			source = source.substring(source.lastIndexOf(">") + 1);
			if (source.lastIndexOf("<") != -1)
				source = source.substring(0, source.lastIndexOf("<") - 1);
		}
		source = source.replace((char) 12288, ' ').trim(); // 将全角空格替换为普通空格
		source = source.replace("[", "").replace("]", "").replace("作者：", "").replaceAll("&nbsp;", "").trim();

		/* 格式化来源信息 */
		int index = -1;
		if ((index = source.indexOf(' ')) != -1) {
			source = source.substring(0, index);
		}
		if ((index = source.indexOf("-")) != -1) {
			source = source.substring(0, index);
		}
		if ((index = source.indexOf("－")) != -1) {
			source = source.substring(0, index);
		}

		info.setBaseInfo(site, title, pubtime, keywords, source);
	}

}
