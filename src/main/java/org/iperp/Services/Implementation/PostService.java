package org.iperp.Services.Implementation;

import org.iperp.Repositories.IPostRepository;
import org.iperp.Services.IPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostService implements IPostService {

    @Autowired
    private IPostRepository postRepository;
}
