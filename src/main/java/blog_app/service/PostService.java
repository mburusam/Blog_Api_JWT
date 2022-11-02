package blog_app.service;

import blog_app.payload.PostDto;
import blog_app.payload.PostResponse;

import java.util.List;

public interface PostService {
    PostDto createPost(PostDto postDto);
   PostResponse getAllPosts(int pageNo, int pageSize,String sortBy,String sortDir);

    PostDto getPostById(long id);

    PostDto updatePost(PostDto postDto, long id);

    void deletePostById(long id);
}
