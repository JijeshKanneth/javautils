package upload;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class FileUpload {
	private ProgressListener listener;
	public void setProgressListener(ProgressListener listener){
		this.listener = listener;
	}
	public void mulipartUpload(HttpServletRequest request, ItemProcessor processor) throws FileUploadException{
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (isMultipart) {
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			if(this.listener != null)
				upload.setProgressListener(this.listener);
			
			List<FileItem> items = upload.parseRequest(request);

			Iterator<FileItem> itr = items.iterator();
			while (itr.hasNext()) {
				FileItem item = itr.next();
				if(item == null) continue;
				processor.process(item);
			}
		} 		
	}
}
