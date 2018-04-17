srcFolder = 'C:/ASM/data/eating/eat_sony/right_usc';
file_path = strcat(srcFolder,'/offsets.csv');
offsets = csvread(file_path);
uva_sub_count = 7;

for sub =1:1    
    sess_count = sum(offsets(sub,:)>0)
    fprintf('Subject: %d, Session count: %d\n', sub, sess_count);
    for sess=1:sess_count
        file_path = strcat(srcFolder,'/subject_', num2str(sub), '/subject', num2str(sub), '_right_session', num2str(sess),'.wada')
        rawdata(uva_sub_count+sub).session(sess).data = csvread(file_path);
        
        file_path = strcat(srcFolder,'/annotations/processed/subject', num2str(sub), '_annotations_right_session', num2str(sess),'.csv');
        rawdata(uva_sub_count+sub).session(sess).annotations = csvread(file_path);
        
        rawdata(uva_sub_count+1).session(sess).offset = offsets(sub, sess);
        
    end
end

save('rawdata', 'rawdata');
