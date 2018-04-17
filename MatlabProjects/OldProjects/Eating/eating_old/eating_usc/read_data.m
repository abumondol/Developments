srcFolder = 'C:/Users/mm5gg/Box Sync/Data Sets/Eating/usc_right';
subjects = {
    {'0301', 59.8},
    {'0601', 26.1},
    {'0602', 16.0},
    {'0603', 17.1},
    {'0801', 82.0},
    {'0803', 67.9},
    {'0901', 63.9},
    {'0902', 123.2},
    {'1303', 123.8},
    {'1304', 107.1},
    {'1305', 135.5}};


file_path = strcat(srcFolder,'/annotations/corrections.csv')
acr = csvread(file_path);

data_usc = [];
for sub =1:length(subjects)
    subject_id = subjects{sub}{1};
    file_path = strcat(srcFolder,'/sensor_data/',subject_id,'_right.wada')
    raw_data = csvread(file_path);

    file_path = strcat(srcFolder,'/annotations/processed/', subject_id, '_annotations_right.csv')
    annots = csvread(file_path);

    
    offset = subjects{sub}{2};

    sub_id = str2num(subject_id);    
    annot_corrections = acr(acr(:,1) == sub_id, :);  
 
    res = process_data(raw_data, offset, annots, annot_corrections);    
    data_usc(sub).subbject_id = subject_id;
    data_usc(sub).accel = res.accel;
    data_usc(sub).grav = res.grav;
    data_usc(sub).annots = res.annots;
    data_usc(sub).annots_adjusted = res.annots_adjusted ;
    data_usc(sub).eat_annot_coutn = res.eat_annot_count;
    data_usc(sub).drink_annot_count = res.drink_annot_count;
    data_usc(sub).non_bite_annot_count = res.non_bite_annot_count;
    data_usc(sub).annot_corrections = res.annot_corrections;
        
end

save('data_usc', 'data_usc');
