function stat = chunk_annot_stat(chunks, annots)
    stat = struct;
    chunk_count = size(chunks, 1);
    annot_count = size(annots, 1);
    
    annot_to_chunk = zeros(annot_count, 1);
    chunk_to_annot_count = zeros(chunk_count, 1);
    chunk_to_annot = zeros(chunk_count, 1);
    
    for i = 1:annot_count
        t = annots(i,1);
        chunk_index = find(t>=chunks(:,3) & t<=chunks(:,4));
        
        if ~isempty(chunk_index)
            annot_to_chunk(i,1) = chunk_index;
            chunk_to_annot_count(chunk_index, 1) = chunk_to_annot_count(chunk_index, 1) + 1;
            chunk_to_annot(chunk_index, 1) = annots(i,2);
        end
    end     
    
    stat.chunks = chunks;
    stat.annots = annots;
    stat.chunk_count = chunk_count;
    stat.annot_count = annot_count;    
    stat.chunk_to_annot = chunk_to_annot;
    stat.annot_to_chunk = annot_to_chunk;
    stat.chunk_to_annot_count = chunk_to_annot_count;
    
    %%%%%%%%% derived from above fields %%%%%%%%%%%%
    stat.annot_count_captured = sum(annot_to_chunk(:,1)~=0);
    stat.annot_count_missed = sum(annot_to_chunk(:,1)==0);
    
    a = chunk_to_annot_count(:,1);
    
    stat.chunk_count_has_annot = sum(chunk_to_annot_count(:,1)~=0);
    stat.chunk_count_has_unique_annot = sum(chunk_to_annot_count(:,1)==1);
    stat.chunk_count_has_duplicate_annot = sum(chunk_to_annot_count(:,1)>1);
    stat.chunk_count_has_no_annot = sum(chunk_to_annot_count(:,1)==0);
    
end




