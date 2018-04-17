function chunk_labels = chunk_annot_mapping(chunks, annots)
    
    chunk_count = size(chunks, 1);
    annot_count = size(annots, 1);    
    
    chunk_labels = zeros(chunk_count, 1);
    
    for i = 1:annot_count
        t = annots(i,1);
        chunk_index = find(t>=chunks(:,3) & t<=chunks(:,4));
        
        if ~isempty(chunk_index)           
            label = annots(i,2);
            chunk_labels(chunk_index, 1) = convert_annot(label);
        end
    end     
end

function label = convert_annot(label)
    if label>0 & label<100
        label = 1;
    elseif label>100 & label<200
        label = 1;
    elseif label>200 & label<300
        label = 1;
    elseif label>300 & label <400
        label = 1;
    else
        label = 0;
    end
        

end




