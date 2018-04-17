function read_raw_data_append(names, append)

fprintf('Reading raw sign data ... \n');

if append == true && exist('signatures.mat','file')==2
    fprintf('Loading existing siganutres ... \n');
    load('signatures');    
elseif append == false && exist('signatures.mat','file') == 2 
    fprintf('Deleting existing siganutres ... creating an empty set of signtures\n');
    delete('signatures.mat');    
    signatures = [];   
else
    fprintf('Creating a empty set of signtures\n');
    signatures = [];    
end

exist_total_sign = length(signatures)
k = exist_total_sign + 1;
total_sub = length(names);

for sub_ind = 1:total_sub  
    folder = strcat('sign_data/',names(sub_ind));    
    files = dir(char(folder));
    
    exist_file_names_sub = {};
    j=1;
    for i = 1:exist_total_sign        
        if strcmpi(signatures(i).subject_name, names(sub_ind))
            exist_file_names_sub(j).file_name = signatures(i).file_name;
            j = j+1;
        end
    end
    length(exist_file_names_sub)
    exist_file_len = length(exist_file_names_sub);    
    serial = exist_file_len + 1;

    for j=3:length(files)  % first two files are . and ..
        file_name = files(j).name;        
        file_path = strcat(folder,'/',file_name);       
        
        %Check if file exists
        flag = false;
        for i = 1:exist_file_len
            if strcmpi(file_name, exist_file_names_sub(i).file_name)
                fprintf('file exist %s \n', file_name);
                flag = true;
                continue;
            end
        end  
        
        if flag
            continue;
        end
        %%%%%%%%%%%%%%%%%                
        
        st = strsplit(file_name,'_');        
        if strcmpi(st{2},names(sub_ind)) == false
            display('**************Name doesnt match*************');
            display('XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX');
            exit(0);
        end
        
        raw_data = csvread(char(file_path));        
        signatures(k).serial = serial;
        signatures(k).raw_data = raw_data;      
        signatures(k).subject_name = st{2};
        signatures(k).subject_index = i;
        signatures(k).file_name = file_name;
        signatures(k).processed = false;
        
        serial = serial + 1;
        k = k + 1;        
    end
    
end

save('signatures','signatures');

fprintf('Reading raw sign data done\n\n');