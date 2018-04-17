raw_data_non_eat = [];
srcFolder = 'C:/ASM/data/eating/our_collected/non_eat_sony/right/';
files = dir(srcFolder);
file_count = length(files)
for i = 3:file_count   
    file_path = strcat(srcFolder, files(i).name);
    fprintf('Processing... %s \n', file_path);
    
    raw_data_non_eat(i-2).session(1).data = csvread(char(file_path));
     
end    


save('raw_data_non_eat', 'raw_data_non_eat');



