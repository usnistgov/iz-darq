package gov.nist.healthcare.iz.darq.parser.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

public class TEST {
	
	public static void main(String[] args) throws IOException {
		toZip(new File("/Users/hnt5/playground/zip_simo"));
	}
	
	public static void toZip(File inputFolder) throws IOException {
		FileOutputStream fstream = new FileOutputStream(new File("/Users/hnt5/playground/myZip.zip"));
	    ZipOutputStream zos = new ZipOutputStream(fstream);
	    addToZip("", zos, inputFolder);
	    zos.close();
	    fstream.close();
	}

	public static void addToZip(String path, ZipOutputStream myZip, File f) throws FileNotFoundException, IOException{
	    if(f.isDirectory()){
	        for(File subF : f.listFiles()){
	            addToZip(path + File.separator + f.getName() , myZip, subF);
	        }
	    }
	    else {
	        ZipEntry e = new ZipEntry(path + File.separator + f.getName());
	        myZip.putNextEntry(e);
	        try (InputStream is = new FileInputStream(f.getAbsolutePath())) {
	            IOUtils.copyLarge(is, myZip);
	        }
	    }
	}

}
