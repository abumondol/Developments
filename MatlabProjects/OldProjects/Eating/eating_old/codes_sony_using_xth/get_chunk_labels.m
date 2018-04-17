function chunk_labels_all = get_chunk_labels(data, chunks_all)
    
    chunk_labels_all = [];
    sub_count = length(chunks_all);
    for sub = 1:sub_count
        sess_count = length(chunks_all(sub).session);
        for sess=1:sess_count
            annots = data(sub).session(sess).annotations;            
            chunks = chunks_all(sub).session(sess).chunks;            
            len = length(chunks);
            labels = zeros(len,1);            
            annot_count = size(annots, 1);
            
            for i = 1:annot_count
                t = annots(i,1);                
                chunk_index = find(t>=chunks(:,3) & t<=chunks(:,4));                
                if ~isempty(chunk_index)                    
                    labels(chunk_index, 1) = annots(i ,2);                    
                end
            end
            chunk_labels_all(sub).session(sess).labels = labels;
        end
    end
    
end


    
    
    