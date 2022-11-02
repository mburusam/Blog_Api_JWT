package blog_app.service.impl;

import blog_app.entity.Post;
import blog_app.exceptions.ResourceNotFoundException;
import blog_app.payload.PostDto;
import blog_app.payload.PostResponse;
import blog_app.repository.PostRepository;
import blog_app.service.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;

    public PostServiceImpl(PostRepository postRepository, ModelMapper modelMapper) {
        this.postRepository = postRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public PostDto createPost(PostDto postDto) {

        //convert postDto to entity
        Post post = mapToEntity(postDto);
        Post newPost = postRepository.save(post);
        //convert post Entity to PostDto
        PostDto postResponse = mapToDTO(newPost);
        return postResponse;
    }

    @Override
    public PostResponse getAllPosts(int pageNo, int pageSize,String sortBy,String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo,pageSize,sort);
        Page<Post>posts = postRepository.findAll(pageable);
        //get content from page object
        List<Post> listOfPosts = posts.getContent();
         List<PostDto>content=listOfPosts.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());

        PostResponse postResponse = new PostResponse();
        postResponse.setContent(content);
        postResponse.setPageNo(posts.getNumber());
        postResponse.setPageSize(posts.getSize());
        postResponse.setTotalElements(posts.getTotalElements());
        postResponse.setTotalPages(posts.getTotalPages());
        postResponse.setLast(posts.isLast());

        return postResponse;

    }

    @Override
    public PostDto getPostById(long id) {
        Post post =postRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Post","id",id));
        return mapToDTO(post);
    }

    @Override
    public PostDto updatePost(PostDto postDto, long id) {
        Post post =postRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Post","id",id));
        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        post.setContent(postDto.getContent());
        Post updatedPost = postRepository.save(post);
        return mapToDTO(updatedPost);
    }

    @Override
    public void deletePostById(long id) {
        Post post =postRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Post","id",id));
        postRepository.delete(post);
    }

    private PostDto mapToDTO(Post post){
        PostDto postDto = modelMapper.map(post,PostDto.class);
        return postDto;
    }

    private Post mapToEntity(PostDto postDto){
        Post post = modelMapper.map(postDto,Post.class);
        return post;
    }
}
