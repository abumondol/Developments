function chunks_all = xth_generate_chunks_all(data, xth, step_size, min_chunk_size_at_xth, min_chunk_size, max_chunk_size)

    chunks_all = [];
    sub_count = length(data);
    for sub = 1:sub_count
        sess_count = length(data(sub).session);
        for sess=1:sess_count
            %fprintf('chunk generation, sub: %d, sess:%d\n',sub, sess);             
            accel = data(sub).session(sess).grav;
            chunks_all(sub).session(sess).chunks = xth_generate_chunks_one_session(accel, xth, step_size, min_chunk_size_at_xth, min_chunk_size, max_chunk_size);
        end
    end
    
end


