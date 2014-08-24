package zip;

public class UnZipProgressListener {
	private long num100Ks = 0;
	private int percentDone = 0;
	private boolean contentLengthKnown = false;
	
	public void update(long bytesRead, long contentLength){
		if (contentLength > -1) {
			contentLengthKnown = true;
		}

		long nowNum100Ks = bytesRead / 100000;
		if (nowNum100Ks > num100Ks) {
			num100Ks = nowNum100Ks;
			if (contentLengthKnown) {
				percentDone = (int) Math.round(100.00 * bytesRead / contentLength);
			}
		}		
	}
	public int getPercentageDone(){
		return percentDone;
	}
}
