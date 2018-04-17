function total_chunks = get_chunk_stat_non_eat(chunks_all)
    total_chunks = 0;
    sub_count = length(chunks_all);
    
    for sub = 1:sub_count
        sess_count = length(chunks_all(sub).session);        
        for sess=1:sess_count           
            chunks = chunks_all(sub).session(sess).chunks;
            chunk_count = size(chunks, 1);            
            total_chunks = total_chunks +  chunk_count;            
        end
    end    
end


    
    
    