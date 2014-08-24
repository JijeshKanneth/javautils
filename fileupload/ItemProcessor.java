package upload;

import org.apache.commons.fileupload.FileItem;

public interface ItemProcessor {
	public void process(FileItem item);
}
