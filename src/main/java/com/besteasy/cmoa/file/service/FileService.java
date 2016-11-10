package com.besteasy.cmoa.file.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.besteasy.cmoa.entity.File;
import com.besteasy.cmoa.file.mapper.FileMapper;
import com.besteasy.cmoa.util.DataProcessUtils;
@Service
public class FileService {
	@Autowired
	private FileMapper fileMapper ;
	
	public void batchInsertFile(List<File> params){
		fileMapper.batchInsertFile(params);
	}

	public List<com.besteasy.cmoa.entity.File> getFileList() {
		return fileMapper.getFileList();
	}

	public Integer saveFile(String realPath) {
		File file = new com.besteasy.cmoa.entity.File();
		String name = DataProcessUtils.subStr(realPath);
		file.setName(name);
		file.setUrl(realPath);
		fileMapper.saveFile(file);
		Integer id = fileMapper.getFileId(realPath);
		return id;
	}

	public void deleteFile(File file,String sourceml) {
		fileMapper.deleteFileById(file);
		java.io.File sourceFolder  = new java.io.File(sourceml);
		System.out.println(sourceml);
		java.io.File[] files = sourceFolder.listFiles();
			for(java.io.File sourcefile:files){
				if(sourcefile.getName().equals(file.getName())){
					sourcefile.delete();
				}
			}
	}


	public com.besteasy.cmoa.entity.File getFileById(Integer id) {
		
		return fileMapper.getFileById(id);
	}
	
}
