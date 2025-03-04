package io.github.svapollo.quarkussocial.rest.dto;

import io.github.svapollo.quarkussocial.domain.model.Post;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PostResponse {

    private String text;
    private LocalDateTime postDateTime;

    public static PostResponse fromEntity(Post post){
        var response = new PostResponse();

        response.setText(post.getText());
        response.setPostDateTime(post.getPostDateTime());

        return response;
    }
}
