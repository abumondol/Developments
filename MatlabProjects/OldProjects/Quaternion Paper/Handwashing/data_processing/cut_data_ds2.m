folder = 'C:/Users/mm5gg/Box Sync/Data Sets/Hand wash/Hand Wash Mobiquitous/sensys_ds1';
src = strcat(folder,'mobiquitous_ds2');
dest = strcat(folder, 'sensys_ds2');

files = dir(src)
for i =3:length(files)
    srcfile = strcat(src, '/', files(i).name)
    destfile = strcat(dest, '/', files(i).name);
    d = csvread(srcfile);
    
    if endsWith(files(i).name, 'mix_hw')
        d = d(1:1+40*50-1, 2:end);
    elseif endsWith(files(i).name, 'not_hw')
        d = d(500:500+140*50-1, 2:end);
    else
        d = d(125:125+10*50-1, 2:end);
    end    
    
    csvwrite(destfile, d);
end

