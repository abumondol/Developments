function [TP, FP, FN] = get_chunk_stat(data, chunks_all)
    total_chunks = 0;
    total_annots = 0;
    total_annot_captured = 0;

    sub_count = length(chunks_all);
    for sub = 1:sub_count
        sess_count = length(chunks_all(sub).session);
        for sess=1:sess_count
            annots = data(sub).session(sess).annotations;
            annots = annots(annots(:, 2)>0 & annots(:, 2)<1000, :);
            chunks = chunks_all(sub).session(sess).chunks;
            
            chunk_count = size(chunks, 1);
            annot_count = size(annots, 1);
            
            chunk_to_annot_count = zeros(chunk_count, 1);            
            for i = 1:annot_count
                t = annots(i,1);
                chunk_index = find(t>=chunks(:,3) & t<=chunks(:,4));
                if ~isempty(chunk_index)                    
                    chunk_to_annot_count(chunk_index, 1) = chunk_to_annot_count(chunk_index, 1) + 1;                    
                end
            end
            annot_captured = sum(chunk_to_annot_count(:,1)>0);
            
            total_chunks = total_chunks +  chunk_count;
            total_annots = total_annots +  annot_count;
            total_annot_captured = total_annot_captured + annot_captured;            
        end
    end
    
    TP = total_annot_captured;
    FP = total_chunks - total_annot_captured;
    FN = total_annots - total_annot_captured;
end  
    
    