package com.zhongxin.sonicshelf.mapper;

import com.zhongxin.sonicshelf.dto.response.CategoriesResponse;
import com.zhongxin.sonicshelf.dto.response.MusicManageResponse;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategoriesMapper {

    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "parent_Id", property = "parentId"),
            @Result(column = "name", property = "name"),
            @Result(column = "description", property = "description")
    })

    @Select("select * from categories where id = #{id}")
    CategoriesResponse selectById(int id);

    @Select("select * from categories where parent_id = #{parentId}")
    List<CategoriesResponse> selectByParentId(int parentId);

    @Select("select * from categories where parent_id IS NULL")
    List<CategoriesResponse> selectParents();

    @Select("SELECT category_id FROM playlist_categories WHERE playlist_id = #{id}")
    List<Long> findByPlaylistId(Long id);

    @Insert("insert into playlist_categories (playlist_id, category_id) VALUES (#{id},#{tag})")
    void addPlaylistTags(@Param("id") Long id, @Param("tag") Long tag);

    @Delete("delete from playlist_categories where playlist_id=#{id} and category_id=#{tag}")
    void removePlaylistTags(@Param("id") Long id, @Param("tag") Long tag);


    List<Long> selectMusicIdByCategoryId(List<Long> ids);

    List<Long> selectCategoryIdsFromMusicCategories(List<Long> ids);

    @Select("select c.id,name from categories as c left join music_categories as mc on c.id = mc.category_id where mc.music_id = #{id}")
    List<MusicManageResponse.Category> selectByMusicId(Long id);

    @Select("SELECT category_id FROM music_categories WHERE music_id = #{id}")
    List<Long> findByMusicId(Long id);

    @Insert("insert into music_categories (music_id,category_id) values (#{id},#{tag})")
    void addMusicTags(@Param("id") Long id, @Param("tag") Long tag);

    @Delete("delete from music_categories where music_id=#{id} and category_id=#{tag}")
    void removeMusicTags(@Param("id") Long id, @Param("tag") Long tag);
}