function res_chunks = xth_chunks_from_big_chunk(a, t, big_chunk, xth, step_size, max_chunk_size)
    if big_chunk(1,4) - big_chunk(1,3) <= max_chunk_size
        res_chunks = [big_chunk, xth];
        return
    end
    
    xth = xth - step_size;
    inside = false;
    start_index = big_chunk(1,1);
    end_index = big_chunk(1,2);
    chunks = [];
    for i=start_index:end_index
        if a(i,1) <= xth && inside == false
            chunks = [chunks; i, i];
            inside = true;
        elseif a(i,1) > xth && inside == true
            chunks(end, 2) = i-1;
            inside = false;            
        end
    end

    if inside == true
        chunks(end, 2) = end_index;    
    end

     
    chunk_count = size(chunks, 1);
    if chunk_count ==0
        res_chunks = [];
        return;
    end
    
    chunks = [chunks, t(chunks(:,1),:), t(chunks(:,2),:)];  
    
    res_chunks = [];
    for i = 1:chunk_count
        res = xth_chunks_from_big_chunk(a, t, chunks(i,:), xth, step_size, max_chunk_size);
        res_chunks =  [res_chunks; res];
    end
    
end
