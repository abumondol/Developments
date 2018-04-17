function chunks = filter_chunks_by_threshold(a, x_th)
    count = size(a,1);   
    i=1;
    while i <= count && a(i,1) <= x_th
        i = i+1; 
    end

    chunks = zeros(count, 2);
    chunk_index = 1;
    inside = false;
    start = i;
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

    if inside == true
        chunks(chunk_index, 2) = count;
    else
        chunk_index = chunk_index-1; 
    end

    chunks = chunks(1:chunk_index, :);
    %chunks = [chunks, chunks(:,2) - chunks(:,1)+1];    
end
