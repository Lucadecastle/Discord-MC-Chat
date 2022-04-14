package top.xujiayao.mcdiscordchat.utils;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import top.xujiayao.mcdiscordchat.Config;

import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

import static top.xujiayao.mcdiscordchat.Main.CONFIG;
import static top.xujiayao.mcdiscordchat.Main.CONFIG_FILE;
import static top.xujiayao.mcdiscordchat.Main.LOGGER;

/**
 * @author Xujiayao
 */
public class ConfigManager {

	public static void init(boolean throwException) throws Exception {
		if (CONFIG_FILE.length() != 0) {
			try {
				load();

				try {
					Utils.testJsonValid();
				} catch (Exception e) {
					LOGGER.error(ExceptionUtils.getStackTrace(e));
					LOGGER.error("Invalid JSON!");
				}

				Config newConfig = new Config();

				if (CONFIG.version != newConfig.version) {
					JsonObject oldConfig = new GsonBuilder()
							.setPrettyPrinting()
							.disableHtmlEscaping()
							.create()
							.fromJson(IOUtils.toString(CONFIG_FILE.toURI(), StandardCharsets.UTF_8), JsonObject.class);

					switch (CONFIG.version) {
						case 1 -> {
							CONFIG.generic.useServerNickname = oldConfig.getAsJsonObject("generic").get("useEffectiveNameInsteadOfUsername").getAsBoolean();

							CONFIG.textsZH.unformattedResponseMessage = oldConfig.getAsJsonObject("textsZH").get("unformattedReferencedMessage").getAsString();
							CONFIG.textsZH.formattedResponseMessage = oldConfig.getAsJsonObject("textsZH").get("formattedReferencedMessage").getAsString();

							CONFIG.textsEN.unformattedResponseMessage = oldConfig.getAsJsonObject("textsEN").get("unformattedReferencedMessage").getAsString();
							CONFIG.textsEN.formattedResponseMessage = oldConfig.getAsJsonObject("textsEN").get("formattedReferencedMessage").getAsString();
						}
					}

					CONFIG.version = newConfig.version;
				}

				update();
				Utils.reloadTexts();
			} catch (Exception e) {
				if (throwException) {
					throw e;
				}

				LOGGER.error(ExceptionUtils.getStackTrace(e));
			}
		} else {
			create();

			LOGGER.error("-----------------------------------------");
			LOGGER.error("Error: The config file cannot be found or is empty!");
			LOGGER.error("错误：找不到配置文件或配置文件为空！");
			LOGGER.error("");
			LOGGER.error("Please follow the documentation to configure MCDiscordChat before restarting the server!");
			LOGGER.error("More information + Docs: https://blog.xujiayao.top/posts/4ba0a17a/");
			LOGGER.error("");
			LOGGER.error("请在重新启动服务器之前按照文档配置 MCDiscordChat！");
			LOGGER.error("更多信息 + 文档：https://blog.xujiayao.top/posts/4ba0a17a/");
			LOGGER.error("");
			LOGGER.error("Stopping the server...");
			LOGGER.error("正在停止服务器...");
			LOGGER.error("-----------------------------------------");

			System.exit(0);
		}
	}

	private static void create() {
		try (FileOutputStream outputStream = new FileOutputStream(CONFIG_FILE)) {
			String jsonString = new GsonBuilder()
					.setPrettyPrinting()
					.disableHtmlEscaping()
					.create()
					.toJson(new Config());

			IOUtils.write(jsonString, outputStream, StandardCharsets.UTF_8);
		} catch (Exception e) {
			LOGGER.error(ExceptionUtils.getStackTrace(e));
		}
	}

	private static void load() {
		try {
			CONFIG = new GsonBuilder()
					.setPrettyPrinting()
					.disableHtmlEscaping()
					.create()
					.fromJson(IOUtils.toString(CONFIG_FILE.toURI(), StandardCharsets.UTF_8), Config.class);
		} catch (Exception e) {
			LOGGER.error(ExceptionUtils.getStackTrace(e));
		}
	}

	public static void update() {
		try (FileOutputStream outputStream = new FileOutputStream(CONFIG_FILE)) {
			String jsonString = new GsonBuilder()
					.setPrettyPrinting()
					.disableHtmlEscaping()
					.create()
					.toJson(CONFIG);

			IOUtils.write(jsonString, outputStream, StandardCharsets.UTF_8);
		} catch (Exception e) {
			LOGGER.error(ExceptionUtils.getStackTrace(e));
		}
	}
}
