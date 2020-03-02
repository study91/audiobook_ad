package com.study91.audiobook.book;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;

import com.study91.audiobook.file.FileFactory;
import com.study91.audiobook.file.IFile;
import com.study91.audiobook.option.IOption;
import com.study91.audiobook.option.OptionManager;

/**
 * 字幕
 */
class BookLyric implements IBookLyric {
	/**
	 * 构造器
	 * @param context 应用程序上下文
	 * @param filename 字幕文件名
	 */
	public BookLyric(Context context, String filename) {
		loadFile(context, filename);
	}

	@Override
	public String getContent(long time) {
		String content = null;
		Map<Long, String> map = getLrcMap();

		Long currentTime = 0L; //初始化当前时间点的键值
		for(Long key : map.keySet()) {
			if(time == currentTime || time < key) {
				//播放时间与当前时间点相同或播放时间小于键值时间时，返回歌词
				content = map.get(currentTime);
				break;
			} else {
				//没有找到时间点时，将当前键值暂存为当前时间点
				currentTime = key;
			}
		}

		//没有找到歌词内容时，表示是最后一条歌词
		if(content == null) {
			content = map.get(currentTime);
		}

		return content;
	}

	/**
	 * 载入文件
	 */
	private void loadFile(Context context, String fileName) {
		if(_lrcMap == null) {
			BufferedReader bufferedReader = null;

			try {
				//读歌词文件
				IOption option = OptionManager.getOption(context);
				IFile file = FileFactory.createFile(context, option.getStorageType(), fileName);
				InputStream stream = file.getInputStream();
				InputStreamReader streamReader = new InputStreamReader(stream, "GBK");
				bufferedReader = new BufferedReader(streamReader);

				_lrcMap = new TreeMap<Long, String>(); //实例化歌词map
				String lrcContent = null;
				while((lrcContent = bufferedReader.readLine()) != null) {
					// 歌词格式1：[00:04.415]Excuse me!^对不起。
					// 歌词格式2：[00:17.04]我种下一颗种子
					Pattern contentPattern = Pattern.compile("\\[\\d+:\\d+\\.\\d+\\].*"); //原始歌词匹配正则表达式（以上两种格式都匹配）
					Matcher matcher = contentPattern.matcher(lrcContent); //匹配原始歌词
					if(matcher.matches()) {
						Long time = parseTime(lrcContent); //解析歌词时间
						String content = parseContent(lrcContent); //解析歌词内容
						_lrcMap.put(time, content); //将歌词时间和歌词内容添加到map中
					}
				}
			} catch (IOException e) {
				throw new RuntimeException(e); // 抛出异常
			} finally {
				if(bufferedReader != null) {
					try {
						bufferedReader.close();
					} catch (IOException e) {
						throw new RuntimeException(e); // 抛出异常
					}
				}
			}
		}
	}

	/**
	 * 获取歌词Map
	 * @return 歌词Map
	 */
	private Map<Long, String> getLrcMap() {
		return _lrcMap;
	}

	/**
	 * 解析歌词内容
	 * @param content 原始歌词内容
	 * @return 歌词内容
	 */
	private String parseContent(String content) {
		String result = null;

		Pattern timePattern = Pattern.compile("\\[\\d+:\\d+\\.\\d+\\]"); //时间匹配正则表达式
		Matcher timeMatcher = timePattern.matcher(content); //匹配时间

		if(timeMatcher.find() && timeMatcher.start() == 0) {
			String timeString = timeMatcher.group(); //得到时间字符串
			result = content.replace(timeString, "").trim(); //去除时间字符串
		}

		return result;
	}

	/**
	 * 解析歌词时间
	 * @param content 原始歌词内容
	 * @return 歌词时间
	 */
	private long parseTime(String content) {
		long time = -1;

		Pattern timePattern = Pattern.compile("\\[\\d+:\\d+\\.\\d+\\]"); //时间匹配正则表达式
		Matcher timeMatcher = timePattern.matcher(content); //匹配时间

		if(timeMatcher.find() && timeMatcher.start() == 0) {
			String timeString = timeMatcher.group(); //得到时间字符串，例如：[00:17.04]或[00:04.415]
			timeString = timeString.replace("[", ""); //清除时间字符串中的“[”
			timeString = timeString.replace("]", ""); //清除时间字符串中的“]”
			timeString = timeString.replace(".", ":"); //将时间字符串中的“.”替换成“:”，以便于进行字符串分隔

			String[] timeArray = timeString.split(":"); //用字符“:”将时间字符串分隔为时间数组
			if(timeArray.length == 3) {
				long m = Long.parseLong(timeArray[0]); //提取分钟值
				long s = Long.parseLong(timeArray[1]); //提取秒值
				long ms = Long.parseLong(timeArray[2]); //提取毫秒值

				// 时间格式：00:17.04的计算方式
				if(timeArray[2].length() ==2) {
					time = (m*60+s) * 1000 + ms * 10;
				}

				// 时间格式：00:04.415的计算方式
				if(timeArray[2].length() == 3) {
					time = (m*60+s) * 1000 + ms;
				}
			}
		}

		return time;
	}

	/**
	 * 歌词Map（属性变量）
	 */
	private Map<Long, String> _lrcMap;

}
