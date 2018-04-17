srcFolder = 'C:/Users/mm5gg/Box Sync/Data Sets/Eating m2fed/usc_meals';
subjects = {
    {'0102', 94.6},
    {'0103',67.5},
    {'0301', 59.8},
    {'0601', 26.1},
    {'0602', 16.0},
    {'0603', 17.1},
    {'0801', 82.0},
    {'0803', 67.9},
    {'0901', 63.9},
    {'0902', 123.2},
    {'1001', 89.5},
    {'1003', 81.5},
    {'1303', 123.8},
    {'1304', 107.1},
    {'1305', 135.5}};

%file_path = strcat(srcFolder,'/annotations/corrections.csv')
%acr = csvread(file_path);

data = [];
for sub =1:length(subjects)
    subject_id = subjects{sub}{1};
    file_path = strcat(srcFolder,'/sensor_data/',subject_id,'_right.wada')
    raw_data = csvread(file_path);

    file_path = strcat(srcFolder,'/annotations/processed/', subject_id, '_annotations_right.csv')
    annots = csvread(file_path);
    
    offset = subjects{sub}{2};

    %sub_id = str2num(subject_id);    
    %annot_corrections = acr(acr(:,1) == sub_id, :);  
 
    res = process_data(raw_data, offset, annots, []);
    if sub ==1
        accel = res.accel;
        grav = res.grav;       
        res.accel = accel(accel(:,1)<42*60, :);
        res.grav = grav(grav(:,1)<42*60, :);        
    end
    
    data(sub).subject_id = subject_id;
    data(sub).accel = res.accel;
    data(sub).grav = res.grav;
    data(sub).annots = res.annots;
    %data(sub).accel_annot_indices = res.accel_annot_indices;
    %data(sub).annots_adjusted = res.annots_adjusted ;
    data(sub).eat_annot_count = res.eat_annot_count;
    data(sub).drink_annot_count = res.drink_annot_count;
    data(sub).non_bite_annot_count = res.non_bite_annot_count;
    %data(sub).annot_corrections = res.annot_corrections;
        
end

save('data', 'data');
