function chunk_data =get_chunk_data(data, data_noneat, chunks_all, chunk_labels_all, chunks_all_noneat)
    chunk_data = [];
    sub_count = length(data);
    k = 1;
    for sub = 1:sub_count
        sess_count = length(data(sub).session);
        for sess = 1:sess_count
            d = data(sub).session(sess).grav;
            chunks = chunks_all(sub).session(sess).chunks;
            labels = chunk_labels_all(sub).session(sess).labels;
            count = size(chunks, 1);
            for i = 1:count
                s = chunks(i,1);
                e = chunks(i,2);
                chunk_data(k).grav = d(s:e, 2:4);
                chunk_data(k).label = labels(i);
                chunk_data(k).sub = sub;
                chunk_data(k).sess = sess;
                chunk_data(k).type = 1;
                k = k+1;
            end            
        end
        
        sess_count = length(data_noneat(sub).session);
        for sess = 1:sess_count
            d = data_noneat(sub).session(sess).grav;
            chunks = chunks_all_noneat(sub).session(sess).chunks;            
            count = size(chunks, 1);
            for i = 1:count
                s = chunks(i,1);
                e = chunks(i,2);
                chunk_data(k).grav = d(s:e, 2:4);
                chunk_data(k).label = 0;
                chunk_data(k).sub = sub;
                chunk_data(k).sess = sess;
                chunk_data(k).type = 0;
                k = k+1;
            end            
        end
    end
    
end