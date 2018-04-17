function features = features_all_chunks(data, chunks_all)        
    features = [];
    sub_count = length(data);    
    for sub =1:sub_count
        sess_count = length(data(sub).session);
        for sess = 1:sess_count            
            accel = data(sub).session(sess).grav;
            chunks = chunks_all(sub).session(sess).chunks;            
            chunk_count = size(chunks, 1);            
            
            f = [];
            for i = 1:chunk_count
                s = chunks(i,1);
                e = chunks(i,2);
                w = accel(s:e, 2:4);
                f = [f; get_features_one_window(w)];
            end
            
            features(sub).session(sess).features = f;            
            
        end        
    end
    
    
end


