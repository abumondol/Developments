offset_session1 = [16.2, 10.5, 9.5, 7.9, 8.0]';
offset_session2 = [7.8, 18.3, 21.1, 16.0, 5.6]';
offset = [offset_session1, offset_session2];
srcFolder = 'C:/ASM/data/eating/eat_sony/right_uva';

raw_data = [];
for sub =1:5
    for sess=1:2
        file_path = strcat(srcFolder,'/subject_', num2str(sub), '/subject', num2str(sub), '_right_session', num2str(sess),'.wada')
        raw_data(sub).session(sess).data = csvread(file_path);
        
        file_path = strcat(srcFolder,'/annotations/processed/subject', num2str(sub), '_annotations_right_session', num2str(sess),'.csv')
        raw_data(sub).session(sess).annotations = csvread(file_path);
        
        raw_data(sub).session(sess).offset = offset(sub, sess);
        
    end
end

save('raw_data', 'raw_data');
