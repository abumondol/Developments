srcFolder = 'C:/Users/mm5gg/Box Sync/MyData/Eating m2fed/uva_noneat_home/';
subjects = {'abu','emi','sarah','meiyi'};

data = [];
data_count = length(data);
sub_count = length(subjects);
for sub = 1:sub_count    
    folder_path = strcat(srcFolder, subjects{sub},'_home_noneat');
    files = dir(folder_path);
    for i = 3:length(files)
        file_path = strcat(folder_path, '/', files(i).name)
        raw_data = csvread(file_path);
        
        annots = [];        
        offset = 0;        
        res = process_data(raw_data, offset, annots);
    
        data_count = data_count + 1;
        data(data_count).subject_id = strcat('ne_',  num2str(sub),'_', num2str(i-2));
        data(data_count).accel = res.accel;       
        data(data_count).annots = res.annots;                 
    end
     
end
save('C:\ASM\DevData\eating\matlab\our_free_data_noneat', 'data');
