%load('data')

session_counts = [2, 5, 2, 2, 4, 5, 1]
srcFolder = 'C:/Users/mm5gg/Box Sync/MyData/Eating m2fed/uva_meals';

file_path = strcat(srcFolder,'/offsets.csv')
all_offsets = csvread(file_path);

sub_count = length(data)

for sub = 1:7
    sess_count = session_counts(sub);
    for sess=1:sess_count
        file_path = strcat(srcFolder,'/subject_', num2str(sub), '/subject', num2str(sub), '_right_session', num2str(sess),'.wada')
        raw_data = csvread(file_path);
        
        file_path = strcat(srcFolder,'/annotations/processed/subject', num2str(sub), '_annotations_right_session', num2str(sess),'.csv')
        annots = csvread(file_path);
        
        offset = all_offsets(sub, sess);
        
        res = process_data(raw_data, offset, annots);
        sub_count = sub_count + 1;
        data(sub_count).subject_id = strcat(num2str(sub), '_',  num2str(sess));
        data(sub_count).accel = res.accel;       
        data(sub_count).annots = res.annots;           
    end    
     
end

save('C:\ASM\DevData\eating\matlab\our_lab_data', 'data');
