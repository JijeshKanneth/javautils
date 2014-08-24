package zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtil {
	private UnZipProgressListener listener;
	public void setProgressListener(UnZipProgressListener listener){
		this.listener = listener;
	}
	public void unZipIt(File file, String outputPath) {
		long fileSize = file.length();
		long bytesRead = 0;
		byte[] buffer = new byte[1024];
		try {
			File folder = new File(outputPath);
			if (!folder.exists()) {
				folder.mkdir();
			}
			ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
			ZipEntry ze = zis.getNextEntry();
			while (ze != null) {
				String fileName = ze.getName();
				File newFile = null;
				if(fileName.indexOf(".") == -1){
					newFile = new File(outputPath + File.separator + fileName + File.separator);
					newFile.mkdirs();
				}else{
					newFile = new File(outputPath + File.separator + fileName);
					new File(newFile.getParent()).mkdirs();
					FileOutputStream fos = new FileOutputStream(newFile);
					
					int len;
					while ((len = zis.read(buffer)) > 0) {
						bytesRead += len;
						fos.write(buffer, 0, len);
					}
					fos.close();					
				}
				if(this.listener != null){
					long accured = ze.getSize() - ze.getCompressedSize();
					if(accured > 0 ) fileSize += accured;
					this.listener.update(bytesRead, fileSize);
				}
				ze = zis.getNextEntry();
			}
			zis.closeEntry();
			zis.close();
		} catch (IOException ex) {
			System.out.println("Error occured while unziping the file "+ex);
		}
	}
}
