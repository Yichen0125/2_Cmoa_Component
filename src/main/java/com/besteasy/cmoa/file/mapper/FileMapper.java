package com.besteasy.cmoa.file.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.besteasy.cmoa.entity.File;

public interface FileMapper {
	
	Integer getFileId(@Param("realPath")String realPath);
	/**
	 * 获取FileList集合
	 * @return
	 */
	List<File> getFileList();
	
	
	/**
	 * 添加数据
	 * @param file
	 */
	void saveFile(File file);
	
	/**
	 * 批量保存
	 * 
	 */
	void batchInsertFile(List<File> params);

	/**
	 * 根据ID 进行文件删除
	 * @param id
	 */
	void deleteFileById(com.besteasy.cmoa.entity.File file);


	File getFileById(@Param("id")Integer id);
	
}
