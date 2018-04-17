function chunks = filter_chunks(chunks, p)
    chunks = chunks(chunks(:,6)>=p.min_chunk_size, :);
end