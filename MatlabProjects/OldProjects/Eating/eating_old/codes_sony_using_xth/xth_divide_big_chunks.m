function chunks = xth_divide_big_chunks(accel, big_chunks, xth, step_size, max_chunk_size)    
    t = accel(:,1);
    a = accel(:, 2:4);        
    chunk_count = size(big_chunks, 1);
    
    chunks = [];    
    for i = 1:chunk_count    
        res = xth_chunks_from_big_chunk(a, t, big_chunks(i,:), xth, step_size, max_chunk_size);
        chunks = [chunks; res];
    end       
    
end