rawdata_noneat = [];
srcFolder = 'C:/ASM/data/eating/noneat_sony/right/';
files = dir(srcFolder);
file_count = length(files)
for i = 3:file_count   
    file_path = strcat(srcFolder, files(i).name);
    fprintf('Processing... %s \n', file_path);
    
    rawdata_noneat(i-2).session(1).data = csvread(char(file_path));
     
end  

save('rawdata_noneat', 'rawdata_noneat');



