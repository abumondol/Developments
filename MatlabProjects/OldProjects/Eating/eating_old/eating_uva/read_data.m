session_counts = [2, 5, 2, 2, 4, 5, 1]
srcFolder = 'C:/Users/mm5gg/Box Sync/Data Sets/Eating/uva_right_sony_ridwan_lab';

file_path = strcat(srcFolder,'/offsets.csv')
offset = csvread(file_path);

raw_data = [];
for sub =1:7
    sess_count = session_counts(sub);
    for sess=1:sess_count
        file_path = strcat(srcFolder,'/subject_', num2str(sub), '/subject', num2str(sub), '_right_session', num2str(sess),'.wada')
        raw_data(sub).session(sess).data = csvread(file_path);
        
        file_path = strcat(srcFolder,'/annotations/processed/subject', num2str(sub), '_annotations_right_session', num2str(sess),'.csv')
        raw_data(sub).session(sess).annotations = csvread(file_path);
        
        raw_data(sub).session(sess).offset = offset(sub, sess);        
    end
    
    %read non_eat session data
    file_path = strcat(srcFolder,'/non_eat/', num2str(sub), '.wada')
    raw_data(sub).session(sess_count+1).data = csvread(file_path);
    raw_data(sub).session(sess_count+1).annotations = [];        
    raw_data(sub).session(sess_count+1).offset = 0;    
end

save('raw_data', 'raw_data');
