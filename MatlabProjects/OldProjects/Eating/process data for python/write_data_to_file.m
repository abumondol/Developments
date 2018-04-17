sub_count = length(data)
for subject = 1:sub_count
    accel = data(subject).accel;
    annots = data(subject).annots;    
    annots_adjusted = data(subject).annots_adjusted;
    
    file_path = strcat('data/', num2str(subject), '.csv')    
    csvwrite(file_path, accel);
    
    if isempty(annots)
        continue
    end
    
    annots = annots(annots(:, 2) <1000, :);
    annots_adjusted = annots_adjusted(annots_adjusted(:, 2) <1000, :);
    %annots(:, 2) = 1;
    
    file_path = strcat('annotations/', num2str(subject), '.csv')    
    csvwrite(file_path, annots);
    
    file_path = strcat('annotations_adjusted/', num2str(subject), '.csv')    
    csvwrite(file_path, annots_adjusted);
    
end

