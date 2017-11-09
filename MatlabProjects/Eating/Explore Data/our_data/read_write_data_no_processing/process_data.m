function res = process_data(data, offset, annots)    
    annots = sortrows(annots);
    if data(1,2) == 0
        data = data(2:end, :);
    end

    data = data(data(:,2)==1, [1, 4:6]);
    data(:,1) = data(:,1)-data(1,1);
    data(:,1) = data(:,1)/1e9;
    
    res.accel = data;    
    if isempty(annots)                                
        res.annots = [];        
    else
        annots = annots(annots(:, 2)<1000, :);
        annots(:,1) = annots(:,1) - offset;
        res.annots = annots;
    end
end    


