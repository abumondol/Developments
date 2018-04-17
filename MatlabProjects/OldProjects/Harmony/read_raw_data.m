function read_raw_data(names)

if exist('hw_raw_data.mat','file') == 2
    return
end

display('Reading raw Hand Wash  data ...');
labels = [];
raw_data_hw = {};
raw_data_nhw = [];

k = 1;
for i = 1:length(names)    
    folder = strcat('data/continuous/',names(i))    
    files = dir(char(folder));
    
    for j=3:length(files)
        file_name = files(j).name;        
        file_path = strcat(folder,'/',file_name);       
        
        st = strsplit(file_name,'_');
        
        if strcmp(st{2},names(i)) == false
            display('Name doesnt match')
            return
        end
        
        p = 1;
        if strcmp(st{3},'Right')
            p = 1;
        elseif strcmp(st{3},'Left')
            p = 2;
        else
            display('Position doesnt match');
            return
        end
        
        t = 1;
        if strcmp(st{4},'HW')
            t = 1;            
            raw_data_hw{k,1} = csvread(char(file_path));
        elseif strcmp(st{4},'NHW')
            t = 2;
            raw_data_nhw = [ raw_data_nhw ; csvread(char(file_path))];
        else
            display('Type doesnt match');
            return
        end
        
        %stime = str2num(char(st(5)));
        %etime = str2num(char(st(6)));
        
        %labels = [labels; i, p, t, stime, etime];
        %raw_data{k,1} = csvread(char(file_path));
        
        k = k + 1;
    end    
end

save('harmony_raw_data','raw_data_hw', 'raw_data_nhw');
display('Reading raw data done');