function chunks = xth_filter_chunks_by_threshold(accel, x_th)
    t = accel(:,1);
    a = accel(:, 2:4);
    
    count = size(a,1);   
    i=1;
    while i <= count && a(i,1) <= x_th
        i = i+1; 
    end
    start = i;
    
    chunks = zeros(count, 2);
    chunk_index = 1;
    inside = false;    
    for i=start:count
        if a(i,1) <= x_th && inside == false
            chunks(chunk_index, 1) = i;
            inside = true;
        elseif a(i,1) > x_th && inside == true
            chunks(chunk_index, 2) = i-1;
            inside = false;
            chunk_index = chunk_index+1; 
        end
    end

%     if inside == true
%         chunks(chunk_index, 2) = count;
%     else
%         chunk_index = chunk_index-1; 
%     end

    chunk_index = chunk_index-1;
   
    chunks = chunks(1:chunk_index, :);
    chunks = [chunks, t(chunks(:,1)), t(chunks(:,2))];    
end
