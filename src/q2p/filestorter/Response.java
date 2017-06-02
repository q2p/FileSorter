package q2p.filestorter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import q2p.filestorter.sorts.Sorter;

final class Response {
	static enum ContentType {
		text_plain("text/plain"),
		text_html("text/html", ".html", ".htm"),
		text_css("text/css", ".css"),
		text_javascript("text/javascript", ".js"),
		audio_aac("audio/aac", ".aac"),
		audio_mpeg("audio/mpeg", ".mp3"), //mp3
		audio_ogg("audio/ogg", ".ogg"),
		audio_flac("audio/x-flac", ".flac"),
		audio_wave("audio/vnd.wave", ".wav"),
		image_gif("image/gif", ".gif"),
		image_jpeg("image/jpeg", ".jpg",".jpeg"),
		image_png("image/png", ".png"),
		image_svg_xml("image/svg+xml", ".svg"),
		image_ico("image/vnd.microsoft.icon", ".ico",".icon"),
		video_mp4("video/mp4", ".mp4"),
		video_webm("video/webm", ".webm"),
		video_flv("video/x-flv", ".flv"),
		video_mpeg("video/mpeg", ".mpeg"),
		application_json("application/json", ".json"),
		application_swf("application/x-shockwave-flash", ".swf");
		
		public final String print;
		public final String[] extentions;
		
		private ContentType(final String print, final String... extentions) {
			this.print = print;
			this.extentions = extentions;
		}
		
		public static ContentType getByExtention(final String path) {
			for(final ContentType type : values())
				if(FilePicker.endsWith(path, type.extentions))
					return type;
			return text_plain;
		}
	}
	
	static OutputStream out;
	
	private static final byte[] done = ("<!DOCTYPE html><html><head><meta charset=\"utf-8\"><title>IS</title><style>html{padding:0px;margin:0px;font-family:sans-serif;font-size:16px;font-weight:normal;color:#dfdfdf;background-color:#0f0f0f}body{padding:50px;text-align:center}</style></head><body>Сортировка окончена</body></html>").getBytes(StandardCharsets.UTF_8);
	
	private static final String contentLength(final String string) {
		return contentLength(string.getBytes(StandardCharsets.UTF_8));
	}
	private static final String contentLength(final byte[] bytes) {
		return "Content-Length: "+bytes.length;
	}
	
	static final void badRequest() {
		try {
			out.write(("HTTP/1.1 400 Bad Request\nContent-Type: text/plain\n"+contentLength("400 Bad Request")+"\nConnection: close\nCache-Control: no-store, must-revalidate\nPragma: no-cache\nExpires: 0\n\n400 Bad Request").getBytes(StandardCharsets.UTF_8));
			out.flush();
		} catch(final Exception e) {}
	}
	static final void notFound() {
		try {
			out.write(("HTTP/1.1 404 Not Found\nContent-Type: text/plain\n"+contentLength("404 Not Found")+"\nConnection: close\nCache-Control: no-store, must-revalidate\nPragma: no-cache\nExpires: 0\n\n404 Not Found").getBytes(StandardCharsets.UTF_8));
			out.flush();
		} catch(final Exception e) {
			FileSorter.abort(e);
		}
	}
	static final void file(final boolean isSecound) {
		if(Sorter.checkIfDone()) {
			badRequest();
			return;
		}
		
		final String path = FilePicker.P+Sorter.getFile(isSecound);

		final File file = new File(path);
		if(file.isFile()) {
			final FileInputStream fis;
			try {
				fis = new FileInputStream(file);
			} catch(final FileNotFoundException e) {
				notFound();
				return;
			}

			try {
				final int off = Math.min(ProcessRequest.MAX_BUFF, fis.available());
				final byte[] buff = new byte[off];
				fis.read(buff);
				fis.close();
				try {
					out.write(("HTTP/1.1 200 OK\nContent-Type: "+ContentType.getByExtention(path).print+"\n"+contentLength(buff)+"\nConnection: close\nCache-Control: no-store, must-revalidate\nPragma: no-cache\nExpires: 0\n\n").getBytes(StandardCharsets.UTF_8));
					out.write(buff);
					out.flush();
				} catch(final Exception e) {
					e.printStackTrace();
				}
			} catch(final Exception e) {
				FileSorter.abort(e);
			}
		} else {
			badRequest();
			return;
		}
	}
	static final void answer(final boolean isSecound) {
		if(Sorter.checkIfDone()) {
			badRequest();
			return;
		}
		
		Sorter.pushAnswer(isSecound);
		try {
			out.write(("HTTP/1.1 303 See Other\nLocation: /\nConnection: close\nCache-Control: no-store, must-revalidate\nPragma: no-cache\nExpires: 0\n\n").getBytes(StandardCharsets.UTF_8));
			out.flush();
		} catch(final Exception e) {}
	}
	public static final void page() {
		if(Sorter.checkIfDone())
			sendPage(done);
		else
			sendPage(("<!DOCTYPE html><html><head><meta charset=\"utf-8\"><title>IS</title><style>*{padding:0px;margin:0px;font-family:sans-serif;font-size:16px;text-decoration:none;font-weight:normal;color:#dfdfdf;text-align:center}html{background-color:#0f0f0f;min-width:768px;width:100%;height:100%;}body{position:relative;width:100%;height:100%;}.container{position:absolute;width:50%;top:0px;bottom:0px;}#left{left:0px}#right{right:0px}a{display:block;width:100%;height:24px}a:hover{color:#ffffff;background-color:#7f00ff;}.path{width:100%;height:24px}.prev{left:0px;right:0px;top:72px;bottom:0px;position:absolute}video{display:block;max-width:100%;max-height:100%;}audio{display:block;width:100%;}img{display:block;max-width:100%;max-height:100%}</style></head><body><div class=\"container\" id=\"left\"><a href=\"file0\">Открыть</a><a href=\"answer0\">Выбрать</a><div class=\"path\">"+Sorter.getFile(false)+"</div><div class=\"prev\">"+formatAnswer(false)+"</div></div><div class=\"container\" id=\"right\"><a href=\"file1\">Открыть</a><a href=\"answer1\">Выбрать</a><div class=\"path\">"+Sorter.getFile(true)+"</div><div class=\"prev\">"+formatAnswer(true)+"</div></div></body></html>").getBytes(StandardCharsets.UTF_8));
	}
	
	private static String formatAnswer(final boolean isSecound) {
		final String path = Sorter.getFile(isSecound);

		if(new File(FilePicker.P+path).isDirectory()) {
			return "Дирректория<br>"+FilePicker.P+path;
		} else {
			if(FilePicker.isImage(path)) {
				return "<img src=\"/file"+(isSecound?1:0)+"\"></img>";
			} else if(FilePicker.isVideo(path)) {
				return "<video preload=\"auto\" controls loop src=\"/file"+(isSecound?1:0)+"\"></video>";
			} else if(FilePicker.isAudio(path)) {
				return "<audio preload=\"auto\" controls loop src=\"/file"+(isSecound?1:0)+"\"></audio>";
			} else if(FilePicker.isSwf(path)) {
				return "<object data=\"/file"+(isSecound?1:0)+"\"></object>";
			} else if(FilePicker.isFlashVideo(path)) {
				return ".FLV<br>"+FilePicker.P+path;
			} else {
				return "Файл<br>"+FilePicker.P+path;
			}
		}
	}
	private static final void sendPage(final byte[] bytes) {
		try {
			out.write(("HTTP/1.1 200 OK\nContent-Type: text/html\n"+contentLength(bytes)+"\nConnection: close\nCache-Control: no-store, must-revalidate\nPragma: no-cache\nExpires: 0\n\n").getBytes(StandardCharsets.UTF_8));
			out.write(bytes);
			out.flush();
		} catch(final Exception e) {}
	}
	public static final void path(final boolean isSecound) {
		if(Sorter.checkIfDone()) {
			badRequest();
			return;
		}
		
		final String path = FilePicker.P+Sorter.getFile(isSecound);
		try {
			out.write(("HTTP/1.1 200 OK\nContent-Type: text/plain\n"+contentLength(path)+"\nConnection: close\nCache-Control: no-store, must-revalidate\nPragma: no-cache\nExpires: 0\n\n"+path).getBytes(StandardCharsets.UTF_8));
			out.flush();
		} catch(final Exception e) {}
	}
}